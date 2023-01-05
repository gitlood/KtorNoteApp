package com.androiddevs.ktornoteapp.core.data.repositories.interfaces

import com.androiddevs.ktornoteapp.core.data.local.entities.Note
import com.androiddevs.ktornoteapp.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<Resource<List<Note>>>

    fun observeNoteByID(noteID: String): Flow<Note>?

    suspend fun insertNote(note: Note)

    suspend fun deleteNote(noteID: String)

    suspend fun deleteLocallyDeletedNoteID(deletedNoteId: String)

    suspend fun getNoteById(noteId: String): Note?

    suspend fun addOwnerToNote(owner: String, noteID: String): Resource<String>

    suspend fun register(email: String, password: String): Resource<String>

    suspend fun login(email: String, password: String): Resource<String>
}