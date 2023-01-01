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

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditNoteViewModelTest : ViewModelTestBase() {

    private lateinit var addEditNoteViewModel: AddEditNoteViewModel

    @Before
    fun setup() {
        addEditNoteViewModel = AddEditNoteViewModel(fakeNotesRepository)
    }

    @Test
    fun `Should Insert Note - When Provided with Note`() {
        //When
        runTest {
            addEditNoteViewModel.insertNote(getNote())
        }

        //Then
        assertThat(fakeNotesRepository.noteDatabase.size).isEqualTo(1)
    }

    @Test
    fun `Should get Note - When ID exists`() {
        //Given
        val note = getNote()

        // When
        runTest {
            addEditNoteViewModel.insertNote(note)
            addEditNoteViewModel.getNoteById(note.id)
        }

        // Then
        assertThat(
            addEditNoteViewModel.note.value.peekContent()
        ).isEqualTo(
            Resource(
                status = Status.SUCCESS,
                data = note,
                message = null
            )
        )
    }


    @Test
    fun `Should get Null - When ID doesn't exist`() {
        //Given
        val note = getNote()

        // When
        runTest {
            addEditNoteViewModel.insertNote(note)
            addEditNoteViewModel.getNoteById("invalid id")
        }

        // Then
        assertThat(
            addEditNoteViewModel.note.value.peekContent()
        ).isEqualTo(
            Resource(
                status = Status.ERROR,
                data = null,
                message = "Note not found"
            )
        )
    }

    private fun getNote(): Note {
        return Note(
            title = "A title",
            content = "Some content",
            date = 0L,
            owners = listOf("Me"),
            color = Color.BLACK.toString()
        )
    }
}