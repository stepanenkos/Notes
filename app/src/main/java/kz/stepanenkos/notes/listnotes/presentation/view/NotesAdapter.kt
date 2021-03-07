package kz.stepanenkos.notes.listnotes.presentation.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import kz.stepanenkos.notes.NoteData
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.listnotes.listeners.NoteClickListener
import kz.stepanenkos.notes.searchnotes.presentation.view.SearchNotesDiffUtilCallback
import kz.stepanenkos.notes.searchnotes.presentation.view.SearchNotesViewHolder

class NotesAdapter(
    private val noteClickListener: NoteClickListener
) : ListAdapter<NoteData, SearchNotesViewHolder>(SearchNotesDiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchNotesViewHolder {
        return SearchNotesViewHolder(
            itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.note_item, parent, false),
            noteClickListener = noteClickListener
        )
    }

    override fun onBindViewHolder(holder: SearchNotesViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}