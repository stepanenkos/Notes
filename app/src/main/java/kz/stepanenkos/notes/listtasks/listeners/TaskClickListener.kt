package kz.stepanenkos.notes.listtasks.listeners

import kz.stepanenkos.notes.common.model.TaskData

interface TaskClickListener {
    fun onTaskClick(taskData: TaskData)
}