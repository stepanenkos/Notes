package kz.stepanenkos.notes.searchnotes.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kz.stepanenkos.notes.NoteData
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.listnotes.listeners.NoteClickListener
import kz.stepanenkos.notes.searchnotes.presentation.view.SearchNotesAdapter

class SearchNotesFragment : Fragment(), NoteClickListener {
    private lateinit var recyclerView: RecyclerView
    private val notesAdapter = SearchNotesAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_search_notes, container, false)
        recyclerView = root.findViewById(R.id.search_notes_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.adapter = notesAdapter

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bottom_nav_menu, menu)
        setupSearchView(menu.findItem(R.id.searchNotesFragment).actionView as SearchView)
    }

    private fun setupSearchView(searchView: SearchView) {

        searchView.queryHint = "Введите текст для поиска заметки"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                Toast.makeText(requireContext(), "$newText", Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(requireContext(), "$query", Toast.LENGTH_SHORT).show()
                return true
            }

        })
    }

    override fun onNoteClick(noteData: NoteData) {

    }
}