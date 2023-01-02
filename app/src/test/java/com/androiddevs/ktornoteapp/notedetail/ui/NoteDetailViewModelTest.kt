package com.androiddevs.ktornoteapp.notedetail.ui

import app.cash.turbine.Event
import app.cash.turbine.test
import com.androiddevs.ktornoteapp.ViewModelTestBase
import com.androiddevs.ktornoteapp.core.util.Status
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteDetailViewModelTest : ViewModelTestBase() {

    private lateinit var noteDetailViewModel: NoteDetailViewModel

    @Before
    fun setup() {
        noteDetailViewModel = NoteDetailViewModel(fakeNotesRepository)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun end() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Owner being added - When owner and note ID is Populated`() = runTest {
        //When
        noteDetailViewModel.addOwnerToNote("email@gmail.com", "123")

        //Then
        noteDetailViewModel.addOwnerStatus.value.peekContent().run {
            assertThat(message).isNull()
            assertThat(data).isNull()
            assertThat(status).isEqualTo(Status.SUCCESS)
        }
    }

    @Test
    fun `Owner failing to be added to note - When owner or note ID is Empty`() {
        //When
        noteDetailViewModel.addOwnerToNote("", "")

        //Then
        noteDetailViewModel.addOwnerStatus.value.peekContent().run {
            assertThat(message).isEqualTo("The owner can't be empty")
            assertThat(data).isNull()
            assertThat(status).isEqualTo(Status.ERROR)
        }
    }

    @Test
    fun `Observe note id success - When note with noteID is in database`() = runTest {
        //Given
        fakeNotesRepository.insertNote(getNote())

        //When
        val note = noteDetailViewModel.observeNoteByID(getNote().id)?.first()

        //Then
        assertThat(note).isEqualTo(getNote())
    }

    @Test
    fun `Observe note id failure - When note with noteID is not found in database`() = runTest {
        //Given
        fakeNotesRepository.insertNote(getNote())

        //When
        val note = noteDetailViewModel.observeNoteByID("invalid ID")

        //Then
        note?.test {
            val actual = cancelAndConsumeRemainingEvents()
            assertThat(actual).containsExactly(Event.Complete)
        }
    }
}
