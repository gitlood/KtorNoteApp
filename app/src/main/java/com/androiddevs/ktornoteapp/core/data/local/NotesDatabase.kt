package com.androiddevs.ktornoteapp.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.androiddevs.ktornoteapp.core.data.local.entities.LocallyDeletedNoteId
import com.androiddevs.ktornoteapp.core.data.local.entities.Note

@Database(
    entities = [Note::class, LocallyDeletedNoteId::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NotesDatabase : RoomDatabase(){
    abstract fun noteDao(): NoteDao
}