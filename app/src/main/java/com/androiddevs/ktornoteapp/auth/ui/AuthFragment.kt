package com.androiddevs.ktornoteapp.auth.ui

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.core.data.remote.BasicAuthInterceptor
import com.androiddevs.ktornoteapp.core.ui.BaseFragment
import com.androiddevs.ktornoteapp.core.util.Constants.KEY_LOGGED_IN_EMAIL
import com.androiddevs.ktornoteapp.core.util.Constants.KEY_LOGGED_IN_PASSWORD
import com.androiddevs.ktornoteapp.core.util.Constants.NO_EMAIL
import com.androiddevs.ktornoteapp.core.util.Constants.NO_PASSWORD
import com.androiddevs.ktornoteapp.core.util.Resource
import com.androiddevs.ktornoteapp.core.util.Status
import com.androiddevs.ktornoteapp.databinding.FragmentAuthBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment : BaseFragment(R.layout.fragment_auth) {

    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var basicAuthInterceptor: BasicAuthInterceptor

    private var curEmail: String? = null
    private var curPassword: String? = null

    private lateinit var binding: FragmentAuthBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkIfUserIsLoggedIn()

        requireActivity().requestedOrientation = SCREEN_ORIENTATION_PORTRAIT

        subscribeToObservers()

        binding.btnLogin.setOnClickListener {
            val email = binding.etLoginEmail.text.toString()
            val password = binding.etLoginPassword.text.toString()
            viewModel.login(email, password)
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.etRegisterEmail.text.toString()
            val password = binding.etRegisterPassword.text.toString()
            val confirmedPassword = binding.etRegisterPasswordConfirm.text.toString()
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
        binding.registerProgressBar.visibility = View.GONE
        showSnackBar(result.data ?: "Successfully Registered an Account")
    }

    private fun onRegisterLoading() {
        binding.registerProgressBar.visibility = View.VISIBLE
    }

    private fun onRegisterError(result: Resource<String>) {
        binding.registerProgressBar.visibility = View.GONE
        showSnackBar(result.message ?: "An Unknown Error Occurred")
    }

    private fun onLoginSuccess(result: Resource<String>) {
        binding.loginProgressBar.visibility = View.GONE
        showSnackBar(result.data ?: "Successfully Logged In")
        sharedPref.edit().putString(KEY_LOGGED_IN_EMAIL, curEmail).apply()
        sharedPref.edit().putString(KEY_LOGGED_IN_PASSWORD, curPassword).apply()
        authenticateApi(curEmail ?: "", curPassword ?: "")
        navigateToNotes()
    }

    private fun onLoginLoading() {
        binding.loginProgressBar.visibility = View.VISIBLE
    }

    private fun onLoginError(result: Resource<String>) {
        binding.loginProgressBar.visibility = View.GONE
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

    private fun subscribeToObservers() = lifecycleScope.launch {
        viewModel.loginStatus.collectLatest { loginStatus ->
            onLoginStatus(loginStatus)
        }
        viewModel.registerStatus.collectLatest { registerStatus ->
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
                Status.WAITING -> {
                    /* NO_OP */
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
                Status.WAITING -> {}
            }
        }
    }
}