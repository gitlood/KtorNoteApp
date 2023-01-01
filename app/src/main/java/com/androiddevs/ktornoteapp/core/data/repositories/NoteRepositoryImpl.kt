package com.androiddevs.ktornoteapp.core.data.repositories

import android.app.Application
import com.androiddevs.ktornoteapp.core.data.local.NoteDao
import com.androiddevs.ktornoteapp.core.data.local.entities.LocallyDeletedNoteId
import com.androiddevs.ktornoteapp.core.data.local.entities.Note
import com.androiddevs.ktornoteapp.core.data.remote.NoteApi
import com.androiddevs.ktornoteapp.core.data.remote.requests.AccountRequest
import com.androiddevs.ktornoteapp.core.data.remote.requests.AddOwnerRequest
import com.androiddevs.ktornoteapp.core.data.remote.requests.DeleteNoteRequest
import com.androiddevs.ktornoteapp.core.data.repositories.interfaces.NoteRepository
import com.androiddevs.ktornoteapp.core.util.Resource
import com.androiddevs.ktornoteapp.core.util.checkForInternetConnection
import com.androiddevs.ktornoteapp.core.util.networkBoundResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val noteApi: NoteApi,
    private val context: Application
) : NoteRepository {

    override var curNotesResponse: Response<List<Note>>? = null

    override fun getAllNotes(): Flow<Resource<List<Note>>> {
        return networkBoundResource(
            query = {
                noteDao.getAllNotes()
            },
            fetch = {
                syncNotes()
                curNotesResponse
            },
            saveFetchResult = { response ->
                response?.body()?.let {
                    insertNotes(it.onEach { note -> note.isSynced = true })
                }
            },
            shouldFetch = {
                checkForInternetConnection(context)
            }
        )
    }

    override fun observeNoteByID(noteID: String): Flow<Note>? = noteDao.observeNoteById(noteID)

    override suspend fun insertNote(note: Note) {
        val response = addNote(note)
        if (response != null && response.isSuccessful) {
            noteDao.insertNote(note.apply { isSynced = true })
        } else {
            noteDao.insertNote(note)
        }
    }

    override suspend fun deleteNote(noteID: String) {
        val response = try {
            noteApi.deleteNote(DeleteNoteRequest(noteID))
        } catch (e: Exception) {
            null
        }
        noteDao.deleteNoteById(noteID)
        if (response == null || !response.isSuccessful) {
            noteDao.insertLocallyDeletedNoteID(LocallyDeletedNoteId(noteID))
        } else {
            deleteLocallyDeletedNoteID(noteID)
        }
    }

    override suspend fun deleteLocallyDeletedNoteID(deletedNoteId: String) {
        noteDao.deleteNoteById(deletedNoteId)
    }

    override suspend fun getNoteById(noteId: String) = noteDao.getNoteById(noteId)

    override suspend fun addOwnerToNote(owner: String, noteID: String) =
        withContext(Dispatchers.IO) {
            try {
                val response = noteApi.addOwnerToNote(AddOwnerRequest(owner, noteID))
                if (response.isSuccessful && response.body()!!.successful) {
                    Resource.success(response.body()?.message)
                } else {
                    Resource.error(response.body()?.message ?: response.message(), null)
                }
            } catch (e: Exception) {
                Resource.error(
                    "Couldn't connect to the servers. Check your internet connection",
                    null
                )
            }
        }

    override suspend fun register(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val response = noteApi.register(AccountRequest(email, password))
            if (response.isSuccessful && response.body()!!.successful) {
                Resource.success(response.body()?.message)
            } else {
                Resource.error(response.body()?.message ?: response.message(), null)
            }
        } catch (e: Exception) {
            Resource.error("Couldn't connect to the servers. Check your internet connection", null)
        }
    }

    override suspend fun login(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val response = noteApi.login(AccountRequest(email, password))
            if (response.isSuccessful && response.body()!!.successful) {
                Resource.success(response.body()?.message)
            } else {
                Resource.error(response.body()?.message ?: response.message(), null)
            }
        } catch (e: Exception) {
            Resource.error("Couldn't connect to the servers. Check your internet connection", null)
        }
    }

    override suspend fun syncNotes() {
        val locallyDeletedNoteIds = noteDao.getAllLocallyDeletedNoteIDS()
        locallyDeletedNoteIds.forEach { id -> deleteNote(id.deletedNoteID) }

        val unsyncedNotes = noteDao.getAllUnsyncedNotes()
        unsyncedNotes.forEach { note -> insertNote(note) }

        curNotesResponse = noteApi.getNotes()
        curNotesResponse?.body()?.let { notes ->
            noteDao.deleteAllNotes()
            insertNotes(notes.onEach { note -> note.isSynced = true }
            )
        }
    }

    override suspend fun addNote(note: Note): Response<ResponseBody>? {
        val response = try {
            noteApi.addNote(note)
        } catch (e: Exception) {
            null
        }
        return response
    }

    override suspend fun insertNotes(notes: List<Note>) {
        notes.forEach {
            insertNote(it)
        }
    }
}