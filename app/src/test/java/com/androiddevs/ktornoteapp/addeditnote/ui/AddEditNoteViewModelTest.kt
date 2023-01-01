package com.androiddevs.ktornoteapp.addeditnote.ui

import android.graphics.Color
import com.androiddevs.ktornoteapp.ViewModelTestBase
import com.androiddevs.ktornoteapp.core.data.local.entities.Note
import com.androiddevs.ktornoteapp.core.util.Resource
import com.androiddevs.ktornoteapp.core.util.Status
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class AddEditNoteViewModelTest : ViewModelTestBase() {

    private lateinit var addEditNoteViewModel: AddEditNoteViewModel

    @Before
    fun setup() {
        addEditNoteViewModel = AddEditNoteViewModel(fakeNotesRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Should Insert Note - When Provided with Note`() {

        //Given
        fakeNotesRepository.noteDatabase.clear()

        val note = Note(
            title = "A title",
            content = "Some content",
            date = 0L,
            owners = listOf("Me"),
            color = Color.BLACK.toString()
        )

        //When
        runTest {
            addEditNoteViewModel.insertNote(note)
        }

        //Then
        assertThat(fakeNotesRepository.noteDatabase.size == 1).isTrue()
    }

    @Test
    fun `Should get Note - When ID exists`() {
        //Given
        fakeNotesRepository.noteDatabase.clear()

        val note = Note(
            title = "A title",
            content = "Some content",
            date = 0L,
            owners = listOf("Me"),
            color = Color.BLACK.toString()
        )

        // When
        runTest {
            addEditNoteViewModel.insertNote(note)
            addEditNoteViewModel.getNoteById(note.id)
        }

        // Then
        assertThat(
            addEditNoteViewModel.note.value.peekContent() ==
                    Resource(
                        status = Status.SUCCESS,
                        data = note,
                        message = null
                    )

        ).isTrue()
    }


    @Test
    fun `Should get Null - When ID doesn't exist`() {
        //Given
        fakeNotesRepository.noteDatabase.clear()

        val note = Note(
            title = "A title",
            content = "Some content",
            date = 0L,
            owners = listOf("Me"),
            color = Color.BLACK.toString()
        )

        // When
        runTest {
            addEditNoteViewModel.insertNote(note)
            addEditNoteViewModel.getNoteById("invalid id")
        }

        // Then
        assertThat(
            addEditNoteViewModel.note.value.peekContent() == Resource(
                status = Status.ERROR,
                data = null,
                message = "Note not found"
            )

        ).isTrue()
    }
}