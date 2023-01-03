package com.androiddevs.ktornoteapp.notes.ui

import app.cash.turbine.test
import com.androiddevs.ktornoteapp.ViewModelTestBase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModelTest : ViewModelTestBase() {

    private lateinit var notesViewModel: NotesViewModel

    @Before
    fun setup() {
        notesViewModel = NotesViewModel(fakeNotesRepository)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun end() {
        Dispatchers.resetMain()
    }

    @Test
    fun `All notes are synced - when _forceUpdate is triggered`() =
        runTest {
            //Given
            fakeNotesRepository.insertNote(getNote())

            //When
            launch {
                notesViewModel.syncAllNotes()
            }
            //Then
            launch {
                notesViewModel.allNotes.test {
                    this.awaitItem()
                }
            }
        }

    @Test
    fun `Note is inserted - when provided with note`() {
        //When
        notesViewModel.insertNote(getNote())

        //Then
        assertThat(fakeNotesRepository.noteDatabase.contains(getNote())).isTrue()
    }

    @Test
    fun `Note is deleted = when provided with existing ID`() = runTest {
        //Given
        val note = getNote()
        fakeNotesRepository.insertNote(note)

        //When
        notesViewModel.deleteNote(note.id)

        //Then
        assertThat(fakeNotesRepository.noteDatabase).isEmpty()
    }

    @Test
    fun `Note is deleted is local database = when provided with existing ID`() = runTest {
        //Given
        val note = getNote()
        fakeNotesRepository.insertNote(note)

        //When
        notesViewModel.deleteLocallyDeletedNoteId(note.id)

        //Then
        assertThat(fakeNotesRepository.noteDatabase).isEmpty()
    }
}
