package kz.stepanenkos.notes.searchnotes.presentation.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import kz.stepanenkos.notes.R
import kz.stepanenkos.notes.common.presentation.BaseViewHolder
import kz.stepanenkos.notes.listnotes.listeners.NoteClickListener
import kz.stepanenkos.notes.listtasks.listeners.TaskClickListener
import kz.stepanenkos.notes.searchnotes.data.AllListItem
import kz.stepanenkos.notes.searchnotes.data.NOTES_TYPE
import kz.stepanenkos.notes.searchnotes.data.TASKS_TYPE

class SearchNotesAdapter(
    private val noteClickListener: NoteClickListener,
    private val taskClickListener: TaskClickListener
) : ListAdapter<AllListItem, BaseViewHolder<AllListItem>>(SearchDiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<AllListItem> {
        return when(viewType) {
            NOTES_TYPE -> {
                SearchNotesViewHolder(
                    itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.note_item, parent, false),
                    noteClickListener = noteClickListener
                )
            }
            else -> {
                SearchTasksViewHolder(
                    itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.task_item, parent, false),
                    taskClickListener = taskClickListener
                )
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<AllListItem>, position: Int) {
        when(getItem(position).type) {
            NOTES_TYPE -> {
                (holder as SearchNotesViewHolder).onBind(getItem(position))
            }
            TASKS_TYPE -> {
                (holder as SearchTasksViewHolder).onBind(getItem(position))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }
}