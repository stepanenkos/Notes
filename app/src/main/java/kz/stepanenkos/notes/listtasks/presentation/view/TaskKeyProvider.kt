package kz.stepanenkos.notes.listtasks.presentation.view

import androidx.recyclerview.selection.ItemKeyProvider
import kz.stepanenkos.notes.TaskData

class TaskKeyProvider (private val adapter: TasksAdapter) : ItemKeyProvider<TaskData>(SCOPE_CACHED) {
    override fun getKey(position: Int): TaskData =
        adapter.currentList[position]
    override fun getPosition(key: TaskData): Int =
        adapter.currentList.indexOfFirst {it == key}
}