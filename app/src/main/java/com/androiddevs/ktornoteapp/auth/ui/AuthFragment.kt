package com.androiddevs.ktornoteapp.auth.ui

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.core.data.remote.BasicAuthInterceptor
import com.androiddevs.ktornoteapp.core.util.Constants.KEY_LOGGED_IN_EMAIL
import com.androiddevs.ktornoteapp.core.util.Constants.KEY_LOGGED_IN_PASSWORD
import com.androiddevs.ktornoteapp.core.util.Constants.NO_EMAIL
import com.androiddevs.ktornoteapp.core.util.Constants.NO_PASSWORD
import com.androiddevs.ktornoteapp.core.util.Resource
import com.androiddevs.ktornoteapp.core.util.Status
import com.androiddevs.ktornoteapp.core.ui.BaseFragment
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment : BaseFragment(R.layout.fragment_auth) {

    private val viewModel: AuthViewModel by viewModels()
    private lateinit var loginProgressBar: ProgressBar
    private lateinit var registerProgressBar: ProgressBar

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var basicAuthInterceptor: BasicAuthInterceptor

    private var curEmail: String? = null
    private var curPassword: String? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkIfUserIsLoggedIn()

        loginProgressBar = view.findViewById(R.id.loginProgressBar)
        registerProgressBar = view.findViewById(R.id.registerProgressBar)

        val loginButton = view.findViewById<Button>(R.id.btnLogin)
        val loginEmail = view.findViewById<TextInputEditText>(R.id.etLoginEmail)
        val loginPassword = view.findViewById<EditText>(R.id.etLoginPassword)

        val registerButton = view.findViewById<Button>(R.id.btnRegister)
        val registerEmail = view.findViewById<TextInputEditText>(R.id.etRegisterEmail)
        val registerPassword = view.findViewById<EditText>(R.id.etRegisterPassword)
        val confirmPassword = view.findViewById<TextInputEditText>(R.id.etRegisterPasswordConfirm)

        requireActivity().requestedOrientation = SCREEN_ORIENTATION_PORTRAIT

        subscribeToObservers()

        loginButton.setOnClickListener {
            val email = loginEmail.text.toString()
            val password = loginPassword.text.toString()
            curEmail = email
            curPassword = password
            viewModel.login(email, password)
        }

        registerButton.setOnClickListener {
            val email = registerEmail.text.toString()
            val password = registerPassword.text.toString()
            val confirmedPassword = confirmPassword.text.toString()
            viewModel.register(email, password, confirmedPassword)
        }
    }

    private fun checkIfUserIsLoggedIn() {
        if (isLoggedIn()) {
            authenticateApi(curEmail ?: "", curPassword ?: "")
            navigateToNotes()
        }
    }

    private fun isLoggedIn(): Boolean {
        curEmail = sharedPref.getString(KEY_LOGGED_IN_EMAIL, NO_EMAIL) ?: NO_EMAIL
        curPassword = sharedPref.getString(KEY_LOGGED_IN_PASSWORD, NO_PASSWORD) ?: NO_PASSWORD
        return curEmail != NO_EMAIL && curPassword != NO_PASSWORD
    }

    private fun onRegisterSuccess(result: Resource<String>) {
        registerProgressBar.visibility = View.GONE
        showSnackBar(result.data ?: "Successfully Registered an Account")
    }

    private fun onRegisterLoading() {
        registerProgressBar.visibility = View.VISIBLE
    }

    private fun onRegisterError(result: Resource<String>) {
        registerProgressBar.visibility = View.GONE
        showSnackBar(result.message ?: "An Unknown Error Occurred")
    }

    private fun onLoginSuccess(result: Resource<String>) {
        loginProgressBar.visibility = View.GONE
        showSnackBar(result.data ?: "Successfully Logged In")
        sharedPref.edit().putString(KEY_LOGGED_IN_EMAIL, curEmail).apply()
        sharedPref.edit().putString(KEY_LOGGED_IN_PASSWORD, curPassword).apply()
        authenticateApi(curEmail ?: "", curPassword ?: "")
        navigateToNotes()
    }

    private fun onLoginLoading() {
        loginProgressBar.visibility = View.VISIBLE
    }

    private fun onLoginError(result: Resource<String>) {
        loginProgressBar.visibility = View.GONE
        showSnackBar(result.message ?: "Unknown Error Occurred")
    }

    private fun navigateToNotes() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.authFragment, true)
            .build()
        findNavController().navigate(
            AuthFragmentDirections.actionAuthFragmentToNotesFragment(),
            navOptions
        )
    }

    private fun authenticateApi(email: String, password: String) {
        basicAuthInterceptor.email = email
        basicAuthInterceptor.password = password
    }

    private fun subscribeToObservers() {
        viewModel.loginStatus.observe(viewLifecycleOwner) { loginStatus ->
            onLoginStatus(loginStatus)
        }
        viewModel.registerStatus.observe(viewLifecycleOwner) { registerStatus ->
            onRegisterStatus(registerStatus)
        }
    }

    private fun onLoginStatus(loginStatus: Resource<String>) {
        loginStatus.let {
            when (loginStatus.status) {
                Status.SUCCESS -> {
                    onLoginSuccess(loginStatus)
                }
                Status.ERROR -> {
                    onLoginError(loginStatus)
                }
                Status.LOADING -> {
                    onLoginLoading()
                }
            }
        }
    }

    private fun onRegisterStatus(registerStatus: Resource<String>) {
        registerStatus.let {
            when (registerStatus.status) {
                Status.SUCCESS -> {
                    onRegisterSuccess(registerStatus)
                }
                Status.ERROR -> {
                    onRegisterError(registerStatus)
                }
                Status.LOADING -> {
                    onRegisterLoading()
                }
            }
        }
    }
}