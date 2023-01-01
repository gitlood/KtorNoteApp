package com.androiddevs.ktornoteapp.notedetail.ui

import com.androiddevs.ktornoteapp.ViewModelTestBase
import org.junit.Before
import org.junit.Test

class NoteDetailViewModelTest : ViewModelTestBase() {

    private lateinit var noteDetailViewModel: NoteDetailViewModel

    @Before
    fun setup() {
        noteDetailViewModel = NoteDetailViewModel(fakeNotesRepository)
    }

    @Test
    fun `Should add Owner To Note - if owner and note ID is in Database`(){
        //Given

    }

}
