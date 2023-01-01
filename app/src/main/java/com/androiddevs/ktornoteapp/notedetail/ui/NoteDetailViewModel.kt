package com.androiddevs.ktornoteapp.notedetail.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.ktornoteapp.core.data.repositories.interfaces.NoteRepository
import com.androiddevs.ktornoteapp.core.util.Event
import com.androiddevs.ktornoteapp.core.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(private val repository: NoteRepository) :
    ViewModel() {

    private val _addOwnerStatus =
        MutableStateFlow<Event<Resource<String>>>(Event(Resource.waiting(null)))
    val addOwnerStatus: StateFlow<Event<Resource<String>>> = _addOwnerStatus.asStateFlow()

    fun addOwnerToNote(owner: String, noteID: String) {
        _addOwnerStatus.update { (Event(Resource.loading(null))) }
        if (owner.isNotEmpty() || noteID.isNotEmpty()) {
            viewModelScope.launch {
                val result = repository.addOwnerToNote(owner, noteID)
                _addOwnerStatus.update { Event(result) }
            }
        } else {
            _addOwnerStatus.update { Event(Resource.error("The owner can't be empty", null)) }
            return
        }
    }

    fun observeNoteByID(noteID: String) = repository.observeNoteByID(noteID)
}