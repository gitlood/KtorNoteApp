package com.androiddevs.ktornoteapp.notedetail.ui

import com.androiddevs.ktornoteapp.ViewModelTestBase
import com.androiddevs.ktornoteapp.core.util.Status
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
    fun `Owner failing to be added to note - if owner or note ID is Empty`() {
        //When
        noteDetailViewModel.addOwnerToNote("", "")

        //Then
        noteDetailViewModel.addOwnerStatus.value.peekContent().run {
            assertThat(message).isEqualTo("The owner can't be empty")
            assertThat(data).isNull()
            assertThat(status).isEqualTo(Status.ERROR)
        }
    }
}
