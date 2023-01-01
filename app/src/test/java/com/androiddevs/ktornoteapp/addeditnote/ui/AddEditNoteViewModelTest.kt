package com.androiddevs.ktornoteapp.addeditnote.ui

import android.graphics.Color
import com.androiddevs.ktornoteapp.ViewModelTestBase
import com.androiddevs.ktornoteapp.core.data.local.entities.Note
import com.androiddevs.ktornoteapp.core.util.Status
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditNoteViewModelTest : ViewModelTestBase() {

    private lateinit var addEditNoteViewModel: AddEditNoteViewModel

    @Before
    fun setup() {
        addEditNoteViewModel = AddEditNoteViewModel(fakeNotesRepository)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun end() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Note added to database - When Provided with Note`() = runTest {
        //When
        addEditNoteViewModel.insertNote(getNote())

        //Then
        assertThat(fakeNotesRepository.noteDatabase.size).isEqualTo(1)
    }

    @Test
    fun `Note loading successfully - When ID exists`() = runTest {
        //Given
        val note = getNote()
        fakeNotesRepository.insertNote(note)

        //When
        addEditNoteViewModel.loadNoteByID(note.id)

        //Then
        addEditNoteViewModel.note.value.peekContent().run {
            assertThat(status).isEqualTo(Status.SUCCESS)
            assertThat(data).isEqualTo(note)
            assertThat(message).isNull()
        }

    }

    @Test
    fun `Note failing to load - When ID doesn't exist`() = runTest {
        //Given
        fakeNotesRepository.insertNote(getNote())

        //When
        addEditNoteViewModel.loadNoteByID("invalid id")

        //Then
        addEditNoteViewModel.note.value.peekContent().run {
            assertThat(status).isEqualTo(Status.ERROR)
            assertThat(data).isNull()
            assertThat(message).isEqualTo("Note not found")
        }
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