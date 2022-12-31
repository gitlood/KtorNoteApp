package com.androiddevs.ktornoteapp.addeditnote.ui

import com.androiddevs.ktornoteapp.FakeNotesRepository
import org.junit.Assert.*

class AddEditNoteViewModelTest(){

    val addEditNoteViewModel = AddEditNoteViewModel(FakeNotesRepository())

}