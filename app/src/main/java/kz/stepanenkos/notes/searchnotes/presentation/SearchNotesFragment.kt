package kz.stepanenkos.notes.searchnotes.presentation

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.common.model.NoteData
import kz.stepanenkos.notes.databinding.FragmentSearchNotesBinding
import kz.stepanenkos.notes.listnotes.listeners.NoteClickListener
import kz.stepanenkos.notes.listnotes.presentation.NOTE_ID
import kz.stepanenkos.notes.searchnotes.presentation.view.SearchNotesAdapter
import org.koin.android.ext.android.inject

class SearchNotesFragment : Fragment(R.layout.fragment_search_notes), NoteClickListener {
    private lateinit var binding: FragmentSearchNotesBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView

    private val searchViewModel: SearchViewModel by inject()
    private val notesAdapter = SearchNotesAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchNotesBinding.bind(view)

        recyclerView = binding.searchNotesRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = notesAdapter

        searchView = binding.fragmentSearchNotesSearchView
        setupSearchView(searchView)

        lifecycleScope.launchWhenStarted {
            searchViewModel.allFoundNotes.onEach { allFoundNotes ->
                notesAdapter.submitList(allFoundNotes)
            }.launchIn(lifecycleScope)
        }
    }

    private fun setupSearchView(searchView: SearchView) {

        searchView.queryHint = getString(R.string.search_notes_fragment_search_hint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                CoroutineScope(Dispatchers.IO).launch {
                    if(newText != null && newText.isNotBlank()) {
                        searchViewModel.searchNoteByText(newText)
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
}