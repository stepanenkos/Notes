package kz.stepanenkos.notes.listtasks.listeners

import kz.stepanenkos.notes.common.model.TaskData

interface TaskCheckedListener {
    fun onCheckedTask(taskData: TaskData, checked: Boolean)
}