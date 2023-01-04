package com.androiddevs.ktornoteapp.notes.ui

import android.content.SharedPreferences
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER
import android.graphics.Canvas
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.ktornoteapp.R
import com.androiddevs.ktornoteapp.core.data.local.entities.Note
import com.androiddevs.ktornoteapp.core.ui.BaseFragment
import com.androiddevs.ktornoteapp.core.util.Constants.KEY_LOGGED_IN_EMAIL
import com.androiddevs.ktornoteapp.core.util.Constants.KEY_LOGGED_IN_PASSWORD
import com.androiddevs.ktornoteapp.core.util.Constants.NO_EMAIL
import com.androiddevs.ktornoteapp.core.util.Constants.NO_PASSWORD
import com.androiddevs.ktornoteapp.core.util.Event
import com.androiddevs.ktornoteapp.core.util.Resource
import com.androiddevs.ktornoteapp.core.util.Status
import com.androiddevs.ktornoteapp.databinding.FragmentNotesBinding
import com.androiddevs.ktornoteapp.notes.ui.adapters.NoteAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class NotesFragment : BaseFragment(R.layout.fragment_notes) {

    private val viewModel: NotesViewModel by viewModels()

    @Inject
    lateinit var sharedPref: SharedPreferences

    private lateinit var noteAdapter: NoteAdapter

    private val swipingItem = MutableLiveData(false)

    private lateinit var binding: FragmentNotesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().requestedOrientation = SCREEN_ORIENTATION_USER

        val fab = view.findViewById<FloatingActionButton>(R.id.fabAddNote)

        setupRecyclerView()
        subscribeToObservers()
        setupSwipeRefreshLayout()

        val menuHost: MenuHost = requireActivity()

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

    private fun setupRecyclerView() = binding.rvNotes.apply {
        noteAdapter = NoteAdapter()
        adapter = noteAdapter
        layoutManager = LinearLayoutManager(requireContext())
        ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(this)
    }

    private fun subscribeToObservers() = lifecycleScope.launch {
        viewModel.allNotes.collectLatest {
            it.let { event ->
                val result = event.peekContent()
                when (result.status) {
                    Status.SUCCESS -> {
                        onSuccess(result)
                    }
                    Status.ERROR -> {
                        onError(event, result)
                    }
                    Status.LOADING -> {
                        onLoading(result)
                    }
                    Status.WAITING -> {}
                }
            }
        }
        swipingItem.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayout.isEnabled = !it
        }
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.syncAllNotes()
        }
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

    private fun onSuccess(result: Resource<List<Note>>) {
        noteAdapter.notes = result.data!!
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun onError(
        event: Event<Resource<List<Note>>>,
        result: Resource<List<Note>>
    ) {
        event.getContentIfNotHandled()?.let { errorResource ->
            errorResource.message?.let { message ->
                showSnackBar(message)
            }
        }
        result.data?.let { notes ->
            noteAdapter.notes = notes
        }
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun onLoading(result: Resource<List<Note>>) {
        result.data?.let { notes ->
            noteAdapter.notes = notes
        }
        binding.swipeRefreshLayout.isRefreshing = true
    }
}