package com.androiddevs.ktornoteapp.ui.dialogues

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.androiddevs.ktornoteapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout

class AddOwnerDialogueFragment : DialogFragment() {

    private var positiveListener: ((String) -> Unit)? = null

    fun setPositiveListener(listener: (String) -> Unit) {
        positiveListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.fragment_note_detail, null)
        val clNoteContainer = view.findViewById<ConstraintLayout>(R.id.clNoteContainer)

        val addOwnerEditText = layoutInflater.inflate(
            R.layout.edit_text_email,
            clNoteContainer,
            false
        ) as TextInputLayout
        return MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_add_person)
            .setTitle("Add Owner To Note")
            .setMessage(
                "Enter an Email of a Person you want to share the note with." +
                        "This person will be able to read and edit the note."
            )
            .setView(addOwnerEditText)
            .setPositiveButton("Add") { _, _ ->
                val email =
                    addOwnerEditText.findViewById<EditText>(R.id.etAddOwnerEmail).text.toString()
                positiveListener?.let { yes ->
                    yes(email)
                }
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.cancel()

            }
            .create()
    }
}