package com.androiddevs.ktornoteapp

import com.androiddevs.ktornoteapp.core.data.local.entities.Note
import com.androiddevs.ktornoteapp.core.data.repositories.interfaces.NoteRepository
import com.androiddevs.ktornoteapp.core.util.Resource
import com.androiddevs.ktornoteapp.core.util.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import retrofit2.Response

class FakeNotesRepository : NoteRepository {

    var noteDatabase: MutableList<Note> = mutableListOf()

    override var curNotesResponse: Response<List<Note>>?
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun getAllNotes(): Flow<Resource<List<Note>>> {
        return flow { Resource(Status.SUCCESS, noteDatabase.toList(), null) }
    }

    override fun observeNoteByID(noteID: String): Flow<Note> {
        return noteDatabase.filter { note ->
            note.id == noteID
        }.asFlow()
    }

    override suspend fun insertNote(note: Note) {
        noteDatabase.add(note)
    }

    override suspend fun deleteNote(noteID: String) {
        noteDatabase.forEach { note ->
            if (note.id == noteID) {
                noteDatabase.remove(note)
            }
        }
    }

    override suspend fun deleteLocallyDeletedNoteID(deletedNoteId: String) {
        noteDatabase.forEach { note ->
            if (note.id == deletedNoteId) {
                noteDatabase.remove(note)
            }
        }
    }

    override suspend fun getNoteById(noteId: String): Note? {
        var note: Note? = null
        noteDatabase.forEach {
            if (it.id == noteId)
                note = it
        }
        return note
    }

    override suspend fun addOwnerToNote(owner: String, noteID: String): Resource<String> {
        return Resource(status = Status.SUCCESS, null, message = null)
    }

    override suspend fun register(email: String, password: String): Resource<String> {
        return Resource.success(null)
    }

    override suspend fun login(email: String, password: String): Resource<String> {
        val anEmail = "email@email.com"
        val aPassword = "password"
        return try {
            if (email == anEmail && password == aPassword) {
                Resource.success(null)
            } else {
                Resource.error("Login failed", null)
            }
        } catch (e: Exception) {
            Resource.error("Couldn't connect to the servers. Check your internet connection", null)
        }
    }

    override suspend fun syncNotes() {
        TODO("Not yet implemented")
    }

    override suspend fun addNote(note: Note): Response<ResponseBody>? {
        TODO("Not yet implemented")
    }

    override suspend fun insertNotes(notes: List<Note>) {
        TODO("Not yet implemented")
    }
}