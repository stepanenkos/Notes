package kz.stepanenkos.notes.listtasks.listeners

import kz.stepanenkos.notes.TaskData

interface TaskClickListener {
    fun onTaskClick(taskData: TaskData)
}