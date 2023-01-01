package com.androiddevs.ktornoteapp.addeditnote.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.ktornoteapp.core.data.local.entities.Note
import com.androiddevs.ktornoteapp.core.data.repositories.interfaces.NoteRepository
import com.androiddevs.ktornoteapp.core.util.Event
import com.androiddevs.ktornoteapp.core.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
@HiltViewModel
class AddEditNoteViewModel @Inject constructor(private val repository: NoteRepository) :
    ViewModel() {

    private val _note = MutableStateFlow<Event<Resource<Note>>>(Event(Resource.waiting(null)))
    val note: StateFlow<Event<Resource<Note>>> = _note.asStateFlow()

    fun insertNote(note: Note) = GlobalScope.launch {
        repository.insertNote(note)
    }

    fun loadNoteByID(noteID: String) = viewModelScope.launch {
        val note = repository.getNoteById(noteID)
        note?.let { theNote ->
            _note.update { Event(Resource.success(theNote)) }
        } ?: _note.update { Event(Resource.error("Note not found", null)) }
    }
}