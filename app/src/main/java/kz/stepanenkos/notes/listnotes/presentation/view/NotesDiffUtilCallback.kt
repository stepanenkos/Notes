package kz.stepanenkos.notes.listnotes.presentation.view

import androidx.recyclerview.widget.DiffUtil
import kz.stepanenkos.notes.common.model.NoteData

class NotesDiffUtilCallback : DiffUtil.ItemCallback<NoteData>() {
    override fun areItemsTheSame(oldItem: NoteData, newItem: NoteData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: NoteData, newItem: NoteData): Boolean {
        return oldItem == newItem
    }
}