package kz.stepanenkos.notes.searchnotes.presentation.view

import androidx.recyclerview.widget.DiffUtil
import kz.stepanenkos.notes.common.model.NoteData

class SearchNotesDiffUtilCallback : DiffUtil.ItemCallback<NoteData>() {
    override fun areItemsTheSame(oldItem: NoteData, newItem: NoteData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: NoteData, newItem: NoteData): Boolean {
        return oldItem == newItem
    }
}