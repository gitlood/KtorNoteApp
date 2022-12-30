package com.androiddevs.ktornoteapp.ui.notedetail

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.ui.BaseFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NoteDetailFragment : BaseFragment(R.layout.fragment_note_detail) {

    private val args: NoteDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<FloatingActionButton>(R.id.fabEditNote).setOnClickListener {
            findNavController().navigate(
                NoteDetailFragmentDirections.actionNoteDetailFragmentToAddEditNoteFragment(args.id)
            )
        }

    }
}