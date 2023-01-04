package com.androiddevs.ktornoteapp.notedetail.ui

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.core.data.local.entities.Note
import com.androiddevs.ktornoteapp.core.ui.BaseFragment
import com.androiddevs.ktornoteapp.core.util.Status
import com.androiddevs.ktornoteapp.databinding.FragmentNoteDetailBinding
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val ADD_OWNER_DIALOG_TAG = "ADD_OWNER_DIALOG_TAG"

@AndroidEntryPoint
class NoteDetailFragment : BaseFragment(R.layout.fragment_note_detail) {

    private val viewModel: NoteDetailViewModel by viewModels()

    private val args: NoteDetailFragmentArgs by navArgs()

    private var _binding: FragmentNoteDetailBinding? = null
    private val binding get() = _binding!!

    private var curNote: Note? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val menuHost: MenuHost = requireActivity()

        _binding = FragmentNoteDetailBinding.inflate(inflater, container, false)

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.note_detail_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.miAddOwner -> showAddOwnerDialog()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObservers()

        binding.fabEditNote.setOnClickListener {
            findNavController().navigate(
                NoteDetailFragmentDirections.actionNoteDetailFragmentToAddEditNoteFragment(args.id)
            )
        }

        if (savedInstanceState != null) {
            val addOwnerDialog = parentFragmentManager.findFragmentByTag(ADD_OWNER_DIALOG_TAG)
                    as AddOwnerDialogueFragment?
            addOwnerDialog?.setPositiveListener {
                addOwnerToCurNote(it)
            }
        }
    }

    private fun showAddOwnerDialog() {
        AddOwnerDialogueFragment(clNoteContainer = binding.clNoteContainer).apply {
            setPositiveListener {
                addOwnerToCurNote(it)
            }
        }.show(parentFragmentManager, ADD_OWNER_DIALOG_TAG)
    }

    private fun addOwnerToCurNote(email: String) {
        curNote?.let { note ->
            viewModel.addOwnerToNote(email, note.id)
        }
    }

    private fun setMarkdownText(text: String, tvNoteContent: MaterialTextView) {
        val markwon = Markwon.create(requireContext())
        val markdown = markwon.toMarkdown(text)
        markwon.setParsedMarkdown(tvNoteContent, markdown)
    }

    private fun subscribeToObservers() = lifecycleScope.launch {
        viewModel.addOwnerStatus.collectLatest { event ->
            event.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        binding.addOwnerProgressBar.visibility = View.GONE
                        showSnackBar(result.data ?: "Successfully added owner to note")
                    }
                    Status.ERROR -> {
                        binding.addOwnerProgressBar.visibility = View.GONE
                        showSnackBar(result.message ?: "An unknown error occured")
                    }
                    Status.LOADING -> {
                        binding.addOwnerProgressBar.visibility = View.VISIBLE
                    }
                    Status.WAITING -> {
                        binding.addOwnerProgressBar.visibility = View.GONE
                    }
                }
            }
        }
        viewModel.observeNoteByID(args.id)?.collectLatest { note ->
            note.let {
                binding.tvNoteTitle.text = note.title
                setMarkdownText(note.content, binding.tvNoteContent)
                curNote = note
            }
        } ?: showSnackBar("Note not found")
    }

}