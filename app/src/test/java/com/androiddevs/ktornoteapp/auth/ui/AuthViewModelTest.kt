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
    fun `Login should remain waiting - When Email is Empty`() {
        // When
        authViewModel.login("", "password")

        //Then
        assertThat(authViewModel.loginStatus.value == Resource.waiting(null)).isTrue()
    }

    @Test
    fun `Login should remain waiting - When Password is Empty`() {
        //When
        authViewModel.login("email", "")

        //Then
        assertThat(authViewModel.loginStatus.value == Resource.waiting(null)).isTrue()
    }

    @Test
    fun `Login should return Success - When valid email and password combination is provided`() {
        //When
        runTest {
            authViewModel.login("email@email.com", "password")
        }

        //Then
        assertThat(
            authViewModel.loginStatus.value == Resource.success(null)
        ).isTrue()
    }

    @Test
    fun `Login should return Error - When Invalid email and password combination is provided`() {
        //When
        runTest {
            authViewModel.login("email", "wrongpassword")
        }

        //Then
        assertThat(
            authViewModel.loginStatus.value == Resource.error("Login failed", null)
        ).isTrue()
    }

    @Test
    fun `Register should return error - When All Field aren't populated`() {
        //When
        runTest {
            authViewModel.register("", "", "")
        }

        //Then
        assertThat(
            authViewModel.registerStatus.value == Resource.error(
                "Please fill out all the fields",
                null
            )
        ).isTrue()
    }
}