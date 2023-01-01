package com.androiddevs.ktornoteapp.notes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.ktornoteapp.core.data.local.entities.Note
import com.androiddevs.ktornoteapp.core.data.repositories.interfaces.NoteRepository
import com.androiddevs.ktornoteapp.core.util.Event
import com.androiddevs.ktornoteapp.core.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _forceUpdate = MutableStateFlow(false)

    private val _allNotes = _forceUpdate.flatMapLatest {
        repository.getAllNotes()
    }.flatMapLatest {
        MutableStateFlow(Event(it))
    }

    val allNotes: Flow<Event<Resource<List<Note>>>> = _allNotes

    fun syncAllNotes() = _forceUpdate.update { true }

    fun insertNote(note: Note) = viewModelScope.launch {
        repository.insertNote(note)
    }

    fun deleteNote(noteID: String) = viewModelScope.launch {
        repository.deleteNote(noteID)
    }

    fun deleteLocallyDeletedNoteId(deletedNoteId: String) = viewModelScope.launch {
        repository.deleteLocallyDeletedNoteID(deletedNoteId)
    }
}