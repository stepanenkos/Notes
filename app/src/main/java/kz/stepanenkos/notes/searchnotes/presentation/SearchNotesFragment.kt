package kz.stepanenkos.notes.searchnotes.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kz.stepanenkos.notes.NoteData
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.common.firebasedatabase.domain.FirebaseDatabaseRepository
import kz.stepanenkos.notes.listnotes.listeners.NoteClickListener
import kz.stepanenkos.notes.searchnotes.presentation.view.SearchNotesAdapter
import org.koin.android.ext.android.inject

class SearchNotesFragment : Fragment(), NoteClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private val searchViewModel: SearchViewModel by inject()
    private val notesAdapter = SearchNotesAdapter(this)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_search_notes, container, false)
        recyclerView = root.findViewById(R.id.search_notes_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchView = root.findViewById(R.id.fragment_search_notes_search_view)
        setupSearchView(searchView)
        recyclerView.adapter = notesAdapter

        return root
    }

    private fun setupSearchView(searchView: SearchView) {

        searchView.queryHint = "Введите текст для поиска заметки"
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchViewModel.allFoundNotes.observe(viewLifecycleOwner) {allFoundNotes ->
            notesAdapter.submitList(allFoundNotes)
        }
    }

    override fun onNoteClick(noteData: NoteData) {
        val bundle = Bundle().apply {
            putString("ID", noteData.id)
        }
        findNavController().navigate(R.id.editorFragment, bundle)
    }
}