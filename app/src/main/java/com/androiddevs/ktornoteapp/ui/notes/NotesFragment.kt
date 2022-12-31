package com.androiddevs.ktornoteapp.ui.notes

import android.content.SharedPreferences
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER
import android.graphics.Canvas
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.adapters.NoteAdapter
import com.androiddevs.ktornoteapp.other.Constants.KEY_LOGGED_IN_EMAIL
import com.androiddevs.ktornoteapp.other.Constants.KEY_LOGGED_IN_PASSWORD
import com.androiddevs.ktornoteapp.other.Constants.NO_EMAIL
import com.androiddevs.ktornoteapp.other.Constants.NO_PASSWORD
import com.androiddevs.ktornoteapp.other.Status
import com.androiddevs.ktornoteapp.ui.BaseFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotesFragment : BaseFragment(R.layout.fragment_notes) {

    private val viewModel: NotesViewModel by viewModels()

    @Inject
    lateinit var sharedPref: SharedPreferences

    private lateinit var noteAdapter: NoteAdapter

    private lateinit var rvNotes: RecyclerView

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val swipingItem = MutableLiveData(false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().requestedOrientation = SCREEN_ORIENTATION_USER
        val fab = view.findViewById<FloatingActionButton>(R.id.fabAddNote)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        rvNotes = view.findViewById(R.id.rvNotes)
        setupRecyclerView()
        subscribeToObservers()
        setupSwipeRefreshLayout()

        // The usage of an interface lets you inject your own implementation
        val menuHost: MenuHost = requireActivity()

        // Add menu items without using the Fragment Menu APIs
        // Note how we can tie the MenuProvider to the viewLifecycleOwner
        // and an optional Lifecycle.State (here, RESUMED) to indicate when
        // the menu should be visible
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.menu_notes, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.miLogout -> logout()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        noteAdapter.setOnItemClickListener {
            findNavController().navigate(
                NotesFragmentDirections.actionNotesFragmentToNoteDetailFragment(it.id)
            )
        }

        fab.setOnClickListener {
            findNavController().navigate(
                NotesFragmentDirections.actionNotesFragmentToAddEditNoteFragment(
                    ""
                )
            )
        }
    }

    private val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
        0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                swipingItem.postValue(isCurrentlyActive)
            }
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.layoutPosition
            val note = noteAdapter.notes[position]
            viewModel.deleteNote(note.id)
            Snackbar.make(requireView(), "Note was successfully deleted", Snackbar.LENGTH_LONG)
                .apply {
                    setAction("Undo") {
                        viewModel.insertNote(note)
                        viewModel.deleteLocallyDeletedNoteId(note.id)
                    }
                }.show()
        }
    }

    private fun setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.syncAllNotes()
        }
    }

    private fun setupRecyclerView() = rvNotes.apply {
        noteAdapter = NoteAdapter()
        adapter = noteAdapter
        layoutManager = LinearLayoutManager(requireContext())
        ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(this)
    }

    private fun logout() {
        sharedPref.edit().putString(KEY_LOGGED_IN_EMAIL, NO_EMAIL).apply()
        sharedPref.edit().putString(KEY_LOGGED_IN_PASSWORD, NO_PASSWORD).apply()
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.notesFragment, true)
            .build()
        findNavController().navigate(
            NotesFragmentDirections.actionNotesFragmentToAuthFragment(),
            navOptions
        )
    }

    private fun subscribeToObservers() {
        viewModel.allNotes.observe(viewLifecycleOwner) {
            it?.let { event ->
                val result = event.peekContent()
                when (result.status) {
                    Status.SUCCESS -> {
                        noteAdapter.notes = result.data!!
                        swipeRefreshLayout.isRefreshing = false
                    }
                    Status.ERROR -> {
                        event.getContentIfNotHandled()?.let { errorResource ->
                            errorResource.message?.let { message ->
                                showSnackBar(message)
                            }
                        }
                        result.data?.let { notes ->
                            noteAdapter.notes = notes
                        }
                        swipeRefreshLayout.isRefreshing = false
                    }
                    Status.LOADING -> {
                        result.data?.let { notes ->
                            noteAdapter.notes = notes
                        }
                        swipeRefreshLayout.isRefreshing = true
                    }
                }
            }
        }
        swipingItem.observe(viewLifecycleOwner) {
            swipeRefreshLayout.isEnabled = !it
        }
    }
}