package com.androiddevs.ktornoteapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.androiddevs.ktornoteapp.util.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule

open class ViewModelTestBase {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    lateinit var fakeNotesRepository: FakeNotesRepository

    @Before
    fun repoSetup(){
        fakeNotesRepository = FakeNotesRepository()
    }
}