package com.androiddevs.ktornoteapp.core.data.repositories.interfaces

import com.androiddevs.ktornoteapp.core.data.local.entities.Note
import com.androiddevs.ktornoteapp.core.util.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response

interface NoteRepository {
    var curNotesResponse: Response<List<Note>>?

    fun getAllNotes(): Flow<Resource<List<Note>>>

    fun observeNoteByID(noteID: String): Flow<Note>?

    suspend fun insertNote(note: Note)

    suspend fun deleteNote(noteID: String)

    suspend fun deleteLocallyDeletedNoteID(deletedNoteId: String)

    suspend fun getNoteById(noteId: String): Note?

    suspend fun addOwnerToNote(owner: String, noteID: String): Resource<String>

    suspend fun register(email: String, password: String): Resource<String>

    suspend fun login(email: String, password: String): Resource<String>

    suspend fun syncNotes()

    suspend fun addNote(note: Note): Response<ResponseBody>?

    suspend fun insertNotes(notes: List<Note>)
}