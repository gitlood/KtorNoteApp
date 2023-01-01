package com.androiddevs.ktornoteapp.auth.ui

import com.androiddevs.ktornoteapp.ViewModelTestBase
import org.junit.Before
import org.junit.Test

class AuthViewModelTest : ViewModelTestBase() {

    private lateinit var authViewModel: AuthViewModel

    @Before
    fun setup() {
        authViewModel = AuthViewModel(fakeNotesRepository)
    }

    @Test
    fun something(){

    }
}