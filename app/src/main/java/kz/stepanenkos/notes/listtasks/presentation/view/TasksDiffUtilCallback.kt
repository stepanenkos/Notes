package kz.stepanenkos.notes.listtasks.presentation.view

import androidx.recyclerview.widget.DiffUtil
import kz.stepanenkos.notes.common.model.TaskData

class TasksDiffUtilCallback : DiffUtil.ItemCallback<TaskData>() {
    override fun areItemsTheSame(oldItem: TaskData, newItem: TaskData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TaskData, newItem: TaskData): Boolean {
        return oldItem == newItem
    }
}