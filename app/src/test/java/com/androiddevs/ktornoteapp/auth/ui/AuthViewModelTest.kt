package com.androiddevs.ktornoteapp.auth.ui

import com.androiddevs.ktornoteapp.ViewModelTestBase
import com.androiddevs.ktornoteapp.core.util.Resource
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest : ViewModelTestBase() {

    private lateinit var authViewModel: AuthViewModel

    @Before
    fun setup() {
        authViewModel = AuthViewModel(fakeNotesRepository)
    }

    @Test
    fun `Should remain waiting - When Email is Empty`() {
        authViewModel.login("", "password")
        assertThat(authViewModel.loginStatus.value == Resource.waiting(null)).isTrue()
    }

    @Test
    fun `Should remain waiting - When Password is Empty`() {
        authViewModel.login("email", "")
        assertThat(authViewModel.loginStatus.value == Resource.waiting(null)).isTrue()
    }

    @Test
    fun `Should return Success - When valid email and password combination is provided`() {
        runTest {
            authViewModel.login("email@email.com", "password")
        }
        assertThat(
            authViewModel.loginStatus.value == Resource.success(null)
        ).isTrue()
    }

    @Test
    fun `Should return Error - When Invalid email and password combination is provided`() {
        runTest {
            authViewModel.login("email", "wrongpassword")
        }
        assertThat(
            authViewModel.loginStatus.value == Resource.error("Login failed", null)
        ).isTrue()
    }
}