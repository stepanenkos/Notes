package kz.stepanenkos.notes.searchnotes.data

import kz.stepanenkos.notes.common.model.NoteData
import kz.stepanenkos.notes.common.model.TaskData

const val NOTES_TYPE: Int = 1
const val TASKS_TYPE: Int = 2

sealed class AllListItem(
    val type: Int,
) {
    data class NotesListItem(
        val noteData: NoteData,
    ) : AllListItem(NOTES_TYPE)

    data class TasksListItem(
        val taskData: TaskData,
    ) : AllListItem(TASKS_TYPE)
}

