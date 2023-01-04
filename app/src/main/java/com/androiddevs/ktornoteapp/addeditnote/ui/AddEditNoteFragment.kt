package com.androiddevs.ktornoteapp.addeditnote.ui

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.core.data.local.entities.Note
import com.androiddevs.ktornoteapp.core.ui.BaseFragment
import com.androiddevs.ktornoteapp.core.util.Constants.DEFAULT_NOTE_COLOR
import com.androiddevs.ktornoteapp.core.util.Constants.KEY_LOGGED_IN_EMAIL
import com.androiddevs.ktornoteapp.core.util.Constants.NO_EMAIL
import com.androiddevs.ktornoteapp.core.util.Resource
import com.androiddevs.ktornoteapp.core.util.Status
import com.androiddevs.ktornoteapp.databinding.FragmentAddEditNoteBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

const val FRAGMENT_TAG = "AddEditNoteFragment"

@AndroidEntryPoint
class AddEditNoteFragment : BaseFragment(R.layout.fragment_add_edit_note) {

    @Inject
    lateinit var sharedPref: SharedPreferences

    private var curNote: Note? = null
    private var curNoteColor = DEFAULT_NOTE_COLOR

    private val viewModel: AddEditNoteViewModel by viewModels()

    private val args: AddEditNoteFragmentArgs by navArgs()

    private lateinit var binding: FragmentAddEditNoteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.id.isNotEmpty()) {
            viewModel.loadNoteByID(args.id)
            subscribeToObservers()
        }

        savedInstanceState?.let {
            val colorPickerDialog = parentFragmentManager.findFragmentByTag(FRAGMENT_TAG)
                    as ColorPickerDialogueFragment?
            colorPickerDialog?.setPositiveListener {
                changeViewNoteColor(it)
            }
        }

        binding.viewNoteColor.setOnClickListener {
            ColorPickerDialogueFragment().apply {
                setPositiveListener {
                    changeViewNoteColor(it)
                }
            }.show(parentFragmentManager, FRAGMENT_TAG)
        }
    }

    override fun onPause() {
        super.onPause()
        saveNote()
    }

    private fun saveNote() {
        val authEmail = sharedPref.getString(KEY_LOGGED_IN_EMAIL, NO_EMAIL) ?: NO_EMAIL

        val title = binding.etNoteTitle.text.toString()
        val content = binding.etNoteContent.text.toString()

        if (title.isEmpty() || content.isEmpty()) {
            return
        }

        val date = System.currentTimeMillis()
        val color = curNoteColor
        val id = curNote?.id ?: UUID.randomUUID().toString()
        val owners = curNote?.owners ?: listOf(authEmail)

        val note = Note(title, content, date, owners, color, id = id)

        viewModel.insertNote(note)
    }

    private fun changeViewNoteColor(colorString: String) {
        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.circle_shape, null)
        drawable?.let {
            val wrappedDrawable = DrawableCompat.wrap(it)
            val color = Color.parseColor("#${colorString}")
            DrawableCompat.setTint(wrappedDrawable, color)
            binding.viewNoteColor.background = wrappedDrawable
            curNoteColor = colorString
        }
    }

    private fun subscribeToObservers() = lifecycleScope.launch {
        viewModel.note.collectLatest {
            it.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        onSuccess(result)
                    }
                    Status.ERROR -> {
                        onError(result)
                    }
                    Status.LOADING -> {
                        /* NO_OP */
                    }
                    Status.WAITING -> {
                        /* NO_OP */
                    }
                }
            }
        }
    }

    private fun onError(result: Resource<Note>) {
        showSnackBar(result.message ?: "Note not found")
    }

    private fun onSuccess(result: Resource<Note>) {
        val note = result.data!!
        curNote = note
        binding.etNoteTitle.setText(note.title)
        binding.etNoteContent.setText(note.content)
        changeViewNoteColor(note.color)
    }
}