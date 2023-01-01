package com.androiddevs.ktornoteapp.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.ktornoteapp.core.data.repositories.NoteRepositoryImpl
import com.androiddevs.ktornoteapp.core.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: NoteRepositoryImpl) : ViewModel() {

    private val _loginStatus = MutableStateFlow<Resource<String>>(Resource.waiting(null))
    val loginStatus: StateFlow<Resource<String>> = _loginStatus.asStateFlow()

    private val _registerStatus = MutableStateFlow<Resource<String>>(Resource.waiting(null))
    val registerStatus: StateFlow<Resource<String>> = _registerStatus.asStateFlow()

    fun login(email: String, password: String) {
        _loginStatus.update { Resource.loading(null) }
        if (email.isEmpty() || password.isEmpty()) {
            _loginStatus.update { Resource.error("Please fill out all the fields", null) }
            return
        }
        viewModelScope.launch {
            val result = repository.login(email, password)
            _loginStatus.update { result }
        }
    }

    fun register(email: String, password: String, repeatedPassword: String) {
        _registerStatus.update { Resource.loading(null) }
        if (allFieldsPopulated(email, password, repeatedPassword)) {
            _registerStatus.update { Resource.error("Please fill out all the fields", null) }
            return
        }
        if (password != repeatedPassword) {
            _registerStatus.update { Resource.error("The passwords do not match", null) }
            return
        }
        register(email, password)
    }

    private fun register(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.register(email, password)
            _registerStatus.update { result }
        }
    }

    private fun allFieldsPopulated(
        email: String,
        password: String,
        repeatedPassword: String
    ) = email.isEmpty() || password.isEmpty() || repeatedPassword.isEmpty()
}