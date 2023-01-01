package com.androiddevs.ktornoteapp.notedetail.ui

import com.androiddevs.ktornoteapp.ViewModelTestBase
import com.androiddevs.ktornoteapp.core.util.Resource
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class NoteDetailViewModelTest : ViewModelTestBase() {

    private lateinit var noteDetailViewModel: NoteDetailViewModel

    @Before
    fun setup() {
        noteDetailViewModel = NoteDetailViewModel(fakeNotesRepository)
    }

    @Test
    fun `Should return Success - if owner and note ID is in Database`() {
        //Given

    }

    @Test
    fun `Should return Error - if owner or note ID is Empty`() {
        //When
        noteDetailViewModel.addOwnerToNote("", "")

        //Then
        assertThat(noteDetailViewModel.addOwnerStatus.value.peekContent()).isEqualTo(
            Resource.error(
                "The owner can't be empty",
                null
            )
        )

    }

}
