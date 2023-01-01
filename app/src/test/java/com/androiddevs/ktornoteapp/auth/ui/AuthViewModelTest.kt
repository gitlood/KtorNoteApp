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
    fun `Register should return Error - When All Field aren't populated`() {
        //When
        authViewModel.register("", "", "")

        //Then
        assertThat(
            authViewModel.registerStatus.value == Resource.error(
                "Please fill out all the fields",
                null
            )
        ).isTrue()
    }

    @Test
    fun `Register should return Error - When Password and Repeated Password don't match`() {
        //When
        authViewModel.register("email@email.com", "password", "apassword")

        //Then
        assertThat(
            authViewModel.registerStatus.value == Resource.error("The passwords do not match", null)
        ).isTrue()
    }

    @Test
    fun `Register should return Success - When All Fields are populated and password - repeated password match`() {
        //When
        runTest {
            authViewModel.register("email@email.com", "password", "password")
        }

        //Then
        assertThat(
            authViewModel.registerStatus.value == Resource.success("Success")
        ).isTrue()
    }
}