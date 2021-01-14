package kz.stepanenkos.notes.listnotes.presentation.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import kz.stepanenkos.notes.NoteData
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.listnotes.listeners.NoteClickListener

class NotesAdapter(
    private val noteClickListener: NoteClickListener
) : ListAdapter<NoteData, NotesViewHolder>(NotesDiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.note_item, parent, false),
            noteClickListener = noteClickListener
        )
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    fun removeItem(position: Int) {
        notifyItemRemoved(position)
    }
}