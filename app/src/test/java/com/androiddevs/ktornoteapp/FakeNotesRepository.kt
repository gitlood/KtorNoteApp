package com.androiddevs.ktornoteapp

import androidx.lifecycle.LiveData
import com.androiddevs.ktornoteapp.core.data.local.entities.Note
import com.androiddevs.ktornoteapp.core.data.repositories.interfaces.NoteRepository
import com.androiddevs.ktornoteapp.core.util.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response

class FakeNotesRepository:NoteRepository {
    override var curNotesResponse: Response<List<Note>>?
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun getAllNotes(): Flow<Resource<List<Note>>> {
        TODO("Not yet implemented")
    }

    override fun observeNoteByID(noteID: String): LiveData<Note> {
        TODO("Not yet implemented")
    }

    override suspend fun insertNote(note: Note) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteNote(noteID: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteLocallyDeletedNoteID(deletedNoteId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getNoteById(noteId: String): Note? {
        TODO("Not yet implemented")
    }

    override suspend fun addOwnerToNote(owner: String, noteID: String): Resource<String> {
        TODO("Not yet implemented")
    }

    override suspend fun register(email: String, password: String): Resource<String> {
        TODO("Not yet implemented")
    }

    override suspend fun login(email: String, password: String): Resource<String> {
        TODO("Not yet implemented")
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