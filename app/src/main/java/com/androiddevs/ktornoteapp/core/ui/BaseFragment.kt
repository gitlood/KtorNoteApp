package com.androiddevs.ktornoteapp.core.ui

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment(layoutId: Int) : Fragment(layoutId) {

    fun showSnackBar(text: String) {
        Snackbar.make(
            requireActivity().window.decorView.rootView,
            text,
            Snackbar.LENGTH_LONG
        ).show()
    }
}