package kz.stepanenkos.notes.searchnotes.presentation

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.common.model.NoteData
import kz.stepanenkos.notes.common.model.TaskData
import kz.stepanenkos.notes.databinding.FragmentSearchNotesBinding
import kz.stepanenkos.notes.listnotes.listeners.NoteClickListener
import kz.stepanenkos.notes.listnotes.presentation.NOTE_ID
import kz.stepanenkos.notes.listtasks.listeners.TaskClickListener
import kz.stepanenkos.notes.listtasks.presentation.TASK_ID
import kz.stepanenkos.notes.searchnotes.presentation.view.SearchNotesAdapter
import org.koin.android.ext.android.inject

class SearchFragment : Fragment(R.layout.fragment_search_notes), NoteClickListener, TaskClickListener {
    private lateinit var binding: FragmentSearchNotesBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView

    private val searchViewModel: SearchViewModel by inject()
    private val notesAdapter = SearchNotesAdapter(this, this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchNotesBinding.bind(view)

        recyclerView = binding.searchItemsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = notesAdapter

        searchView = binding.fragmentSearchNotesSearchView
        setupSearchView(searchView)

        lifecycleScope.launchWhenStarted {
            searchViewModel.allFoundItems.onEach { allFoundItems ->
                notesAdapter.submitList(allFoundItems)
            }.launchIn(lifecycleScope)
        }

        lifecycleScope.launchWhenCreated {
            searchViewModel.searchError.onEach { exception ->
                showError(exception)
            }.launchIn(lifecycleScope)
        }
    }

    private fun setupSearchView(searchView: SearchView) {

        searchView.queryHint = getString(R.string.search_fragment_search_hint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                CoroutineScope(Dispatchers.IO).launch {
                    if(newText != null && newText.isNotBlank()) {
                        searchViewModel.searchNoteByText(newText)
                        searchViewModel.searchTaskByText(newText)
                    } else {
                        notesAdapter.submitList(listOf())
                    }
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                CoroutineScope(Dispatchers.IO).launch {
                    if(query != null && query.isNotBlank()) {
                        searchViewModel.searchNoteByText(query)
                        searchViewModel.searchTaskByText(query)
                    } else {
                        notesAdapter.submitList(listOf())
                    }
                }
                return true
            }
        })
    }

    override fun onNoteClick(noteData: NoteData) {
        val bundle = Bundle().apply {
            putInt(NOTE_ID, noteData.id)
        }
        findNavController().navigate(R.id.editorNotesFragment, bundle)
    }

    override fun onTaskClick(taskData: TaskData) {
        val bundle = Bundle().apply {
            putInt(TASK_ID, taskData.id)
        }
        findNavController().navigate(R.id.editorTasksFragment, bundle)
    }

    fun showError(exception: FirebaseException) {
        Snackbar.make(requireView(), exception.toString(), Snackbar.LENGTH_SHORT).show()
    }
}