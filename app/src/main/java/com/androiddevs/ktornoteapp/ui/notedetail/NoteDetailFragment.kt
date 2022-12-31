package com.androiddevs.ktornoteapp.ui.notedetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.data.local.entities.Note
import com.androiddevs.ktornoteapp.ui.BaseFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon

@AndroidEntryPoint
class NoteDetailFragment : BaseFragment(R.layout.fragment_note_detail) {

    private val viewModel: NoteDetailViewModel by viewModels()

    private val args: NoteDetailFragmentArgs by navArgs()

    private lateinit var tvNoteTitle: MaterialTextView
    private lateinit var tvNoteContent: MaterialTextView

    private var curNote: Note? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvNoteTitle = view.findViewById(R.id.tvNoteTitle)
        tvNoteContent = view.findViewById(R.id.tvNoteContent)
        subscribeToObservers()
        view.findViewById<FloatingActionButton>(R.id.fabEditNote).setOnClickListener {
            findNavController().navigate(
                NoteDetailFragmentDirections.actionNoteDetailFragmentToAddEditNoteFragment(args.id)
            )
        }
    }

    private fun setMarkdownText(text: String, tvNoteContent: MaterialTextView) {
        val markwon = Markwon.create(requireContext())
        val markdown = markwon.toMarkdown(text)
        markwon.setParsedMarkdown(tvNoteContent, markdown)
    }

    private fun subscribeToObservers() {
        viewModel.observeNoteByID(args.id).observe(viewLifecycleOwner) { note ->
            note?.let {
                tvNoteTitle.text = note.title
                setMarkdownText(note.content, tvNoteContent)
                curNote = note
            } ?: showSnackBar("Note not found")
        }
    }

}