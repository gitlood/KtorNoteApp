package com.androiddevs.ktornoteapp.ui.notedetail

import androidx.lifecycle.ViewModel
import com.androiddevs.ktornoteapp.repositories.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(private val repository: NoteRepository) :
    ViewModel() {

    fun observeNoteByID(noteID: String) = repository.observeNoteByID(noteID)
}