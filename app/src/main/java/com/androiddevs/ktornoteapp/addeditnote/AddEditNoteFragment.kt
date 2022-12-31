package com.androiddevs.ktornoteapp.addeditnote

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.core.data.local.entities.Note
import com.androiddevs.ktornoteapp.core.util.Constants.DEFAULT_NOTE_COLOR
import com.androiddevs.ktornoteapp.core.util.Constants.KEY_LOGGED_IN_EMAIL
import com.androiddevs.ktornoteapp.core.util.Constants.NO_EMAIL
import com.androiddevs.ktornoteapp.core.util.Status
import com.androiddevs.ktornoteapp.ui.BaseFragment
import com.androiddevs.ktornoteapp.ui.dialogues.ColorPickerDialogueFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

const val FRAGMENT_TAG = "AddEditNoteFragment"

@AndroidEntryPoint
class AddEditNoteFragment : BaseFragment(R.layout.fragment_add_edit_note) {

    @Inject
    lateinit var sharedPref: SharedPreferences

    private var curNote: Note? = null
    private var curNoteColor = DEFAULT_NOTE_COLOR

    private lateinit var etNoteTitle: EditText
    private lateinit var etNoteContent: EditText

    private lateinit var viewNoteColor: View

    private val viewModel: AddEditNoteViewModel by viewModels()

    private val args: AddEditNoteFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etNoteTitle = view.findViewById(R.id.etNoteTitle)
        etNoteContent = view.findViewById(R.id.etNoteContent)
        viewNoteColor = view.findViewById<View>(R.id.viewNoteColor)

        if (args.id.isNotEmpty()) {
            viewModel.getNoteById(args.id)
            subscribeToObservers()
        }

       savedInstanceState?.let {
            val colorPickerDialog = parentFragmentManager.findFragmentByTag(FRAGMENT_TAG)
                    as ColorPickerDialogueFragment?
            colorPickerDialog?.setPositiveListener {
                changeViewNoteColor(it)
            }
        }

        viewNoteColor.setOnClickListener {
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

    private fun changeViewNoteColor(colorString: String) {
        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.circle_shape, null)
        drawable?.let {
            val wrappedDrawable = DrawableCompat.wrap(it)
            val color = Color.parseColor("#${colorString}")
            DrawableCompat.setTint(wrappedDrawable, color)
            viewNoteColor.background = wrappedDrawable
            curNoteColor = colorString
        }
    }

    private fun subscribeToObservers() {
        viewModel.note.observe(viewLifecycleOwner) {
            it?.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        val note = result.data!!
                        curNote = note
                        etNoteTitle.setText(note.title)
                        etNoteContent.setText(note.content)
                        changeViewNoteColor(note.color)
                    }
                    Status.ERROR -> {
                        showSnackBar(result.message ?: "Note not found")
                    }
                    Status.LOADING -> {
                        /* NO_OP */
                    }
                }
            }
        }
    }

    private fun saveNote() {
        val authEmail = sharedPref.getString(KEY_LOGGED_IN_EMAIL, NO_EMAIL) ?: NO_EMAIL

        val title = etNoteTitle.text.toString()
        val content = etNoteContent.text.toString()

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
}