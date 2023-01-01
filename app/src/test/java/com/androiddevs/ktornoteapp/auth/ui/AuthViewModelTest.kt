package com.androiddevs.ktornoteapp.auth.ui

import com.androiddevs.ktornoteapp.ViewModelTestBase
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
class AuthViewModelTest : ViewModelTestBase() {

    private lateinit var authViewModel: AuthViewModel

    @Before
    fun setup() {
        authViewModel = AuthViewModel(fakeNotesRepository)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun end() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Login failing - When Email is Empty`() {
        //When
        authViewModel.login("", "password")

        //Then
        authViewModel.loginStatus.value.run {
            assertThat(message).isNull()
            assertThat(data).isNull()
            assertThat(status).isEqualTo(Status.WAITING)
        }
    }

    @Test
    fun `Login failing - When Password is Empty`() {
        //When
        authViewModel.login("email", "")

        //Then
        authViewModel.loginStatus.value.run {
            assertThat(message).isNull()
            assertThat(data).isNull()
            assertThat(status).isEqualTo(Status.WAITING)
        }
    }

    @Test
    fun `Login successful - When valid email and password combination is provided`() =
        runTest {
            //When
            authViewModel.login("email@email.com", "password")

            //Then
            authViewModel.loginStatus.value.run {
                assertThat(message).isNull()
                assertThat(data).isNull()
                assertThat(status).isEqualTo(Status.SUCCESS)
            }
        }

    @Test
    fun `Login returning error - When Invalid email and password combination is provided`() =
        runTest {
            //When
            authViewModel.login("email", "wrongpassword")

            //Then
            authViewModel.loginStatus.value.run {
                assertThat(message).isEqualTo("Login failed")
                assertThat(data).isNull()
                assertThat(status).isEqualTo(Status.ERROR)
            }
        }

    @Test
    fun `Register returning error - When All Field aren't populated`() {
        //When
        authViewModel.register("", "", "")

        //Then
        authViewModel.registerStatus.value.run {
            assertThat(message).isEqualTo("Please fill out all the fields")
            assertThat(data).isNull()
            assertThat(status).isEqualTo(Status.ERROR)
        }
    }

    @Test
    fun `Register should return Error - When Password and Repeated Password don't match`() {
        //When
        authViewModel.register("email@email.com", "password", "apassword")

        //Then
        authViewModel.registerStatus.value.run {
            assertThat(message).isEqualTo("The passwords do not match")
            assertThat(data).isNull()
            assertThat(status).isEqualTo(Status.ERROR)
        }
    }

    @Test
    fun `Register should return Success - When All Fields are populated and password - repeated password match`() =
        runTest {
            //When
            authViewModel.register("email@email.com", "password", "password")

            //Then
            authViewModel.registerStatus.value.run {
                assertThat(message).isNull()
                assertThat(data).isNull()
                assertThat(status).isEqualTo(Status.SUCCESS)
            }
        }
}