package com.androiddevs.ktornoteapp.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.androiddevs.ktornoteapp.core.data.local.entities.LocallyDeletedNoteId
import com.androiddevs.ktornoteapp.core.data.local.entities.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Query("DELETE FROM notes WHERE id =:noteID")
    suspend fun deleteNoteById(noteID: String)

    @Query("DELETE FROM notes WHERE isSynced = 1")
    suspend fun deleteAllSyncedNotes()

    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()

    @Query("SELECT * FROM notes WHERE id = :noteID")
    fun observeNoteById(noteID: String): Flow<Note>?

    @Query("SELECT * FROM notes WHERE id = :noteID")
    suspend fun getNoteById(noteID: String): Note?

    @Query("SELECT * FROM notes ORDER BY date DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE isSynced = 0")
    suspend fun getAllUnsyncedNotes(): List<Note>

    @Query("SELECT * FROM locally_deleted_note_ids")
    suspend fun getAllLocallyDeletedNoteIDS(): List<LocallyDeletedNoteId>

    @Query("DELETE FROM locally_deleted_note_ids WHERE deletedNoteID  = :deletedNoteId")
    suspend fun deleteLocallyDeletedNoteIDs(deletedNoteId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocallyDeletedNoteID(locallyDeletedNoteId: LocallyDeletedNoteId)
}