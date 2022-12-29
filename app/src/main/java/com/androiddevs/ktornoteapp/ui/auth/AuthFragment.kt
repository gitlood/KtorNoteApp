package com.androiddevs.ktornoteapp.ui.auth

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.other.Status
import com.androiddevs.ktornoteapp.ui.BaseFragment
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : BaseFragment(R.layout.fragment_auth) {

    private val viewModel: AuthViewModel by viewModels()
    private lateinit var registerProgressBar: ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerProgressBar = view.findViewById(R.id.registerProgressBar)

        val registerButton = view.findViewById<Button>(R.id.btnRegister)
        val registerEmail = view.findViewById<TextInputEditText>(R.id.etRegisterEmail)
        val registerPassword = view.findViewById<EditText>(R.id.etRegisterPassword)
        val confirmPassword = view.findViewById<TextInputEditText>(R.id.etRegisterPasswordConfirm)

        requireActivity().requestedOrientation = SCREEN_ORIENTATION_PORTRAIT

        subscribeToObservers()

        registerButton.setOnClickListener {
            val email = registerEmail.text.toString()
            val password = registerPassword.text.toString()
            val confirmedPassword = confirmPassword.text.toString()
            viewModel.register(email, password, confirmedPassword)
        }
    }

    private fun subscribeToObservers() {
        viewModel.registerStatus.observe(viewLifecycleOwner, Observer { result ->
            result?.let {
                when (result.status) {
                    Status.SUCCESS -> {
                        registerProgressBar.visibility = View.GONE
                        showSnackBar(result.data ?: "Successfully Registered an Account")
                    }
                    Status.ERROR -> {
                        registerProgressBar.visibility = View.GONE
                        showSnackBar(result.message ?: "An Unknown Error Occurred")
                    }
                    Status.LOADING -> {
                        registerProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }
}