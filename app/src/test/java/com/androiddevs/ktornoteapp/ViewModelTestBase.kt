package com.androiddevs.ktornoteapp

import android.graphics.Color
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.androiddevs.ktornoteapp.core.data.local.entities.Note
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule

open class ViewModelTestBase {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

//    @OptIn(ExperimentalCoroutinesApi::class)
//    @get:Rule
//    var mainCoroutineRule = MainCoroutineRule()

    lateinit var fakeNotesRepository: FakeNotesRepository

    @Before
    fun repoSetup() {
        fakeNotesRepository = FakeNotesRepository()
    }

    fun getNote(): Note {
        return Note(
            title = "A title",
            content = "Some content",
            date = 0L,
            owners = listOf("Me"),
            color = Color.BLACK.toString(),
            id = "anID"
        )
    }
}