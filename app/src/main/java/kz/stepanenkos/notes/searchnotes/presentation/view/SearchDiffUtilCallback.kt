package kz.stepanenkos.notes.searchnotes.presentation.view

import androidx.recyclerview.widget.DiffUtil
import kz.stepanenkos.notes.searchnotes.data.AllListItem

class SearchDiffUtilCallback : DiffUtil.ItemCallback<AllListItem>() {

    override fun areItemsTheSame(oldItem: AllListItem, newItem: AllListItem) =
        when {
            oldItem is AllListItem.NotesListItem && newItem is AllListItem.NotesListItem && oldItem.noteData.id == newItem.noteData.id -> true
            oldItem is AllListItem.TasksListItem && newItem is AllListItem.TasksListItem && oldItem.taskData.id == newItem.taskData.id -> true
            else -> false
        }

    override fun areContentsTheSame(oldItem: AllListItem, newItem: AllListItem): Boolean =
        when {
            oldItem is AllListItem.NotesListItem && newItem is AllListItem.NotesListItem && oldItem.noteData == newItem.noteData -> true
            oldItem is AllListItem.TasksListItem && newItem is AllListItem.TasksListItem && oldItem.taskData == newItem.taskData -> true
            else -> false
        }
}