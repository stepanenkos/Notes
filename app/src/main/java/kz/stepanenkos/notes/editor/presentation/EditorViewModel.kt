package kz.stepanenkos.notes.editor.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kz.stepanenkos.notes.common.firebasedatabase.domain.FirebaseDatabaseRepository
import kz.stepanenkos.notes.common.model.NoteData
import kz.stepanenkos.notes.common.model.ResponseData
import kz.stepanenkos.notes.common.model.TaskData
import kz.stepanenkos.notes.notification.NotificationAlarmHelper
import java.util.*

class EditorViewModel(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository,
    private val notificationAlarmHelper: NotificationAlarmHelper
) : ViewModel() {
    private val _noteById: MutableSharedFlow<NoteData> = MutableSharedFlow(replay = 1)
    val noteById: SharedFlow<NoteData> = _noteById.asSharedFlow()

    private val _taskById: MutableSharedFlow<TaskData> = MutableSharedFlow(replay = 1)
    val taskById: SharedFlow<TaskData> = _taskById.asSharedFlow()

    private val _errorReceiving: MutableSharedFlow<FirebaseFirestoreException> = MutableSharedFlow(replay = 1)
    val errorReceiving: SharedFlow<FirebaseFirestoreException> = _errorReceiving.asSharedFlow()

    suspend fun saveNote(titleNote: String, contentNote: String) {
        if (titleNote.isNotBlank() && contentNote.isNotBlank()) {
            saveNote(
                NoteData(
                    titleNote = titleNote,
                    contentNote = contentNote,
                    searchKeywords = fillNoteSearchKeywordsList(titleNote, contentNote)
                )
            )
        }
    }

    private suspend fun saveNote(noteData: NoteData) {
        firebaseDatabaseRepository.saveNote(noteData)
    }

    suspend fun saveTask(taskData: TaskData) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseDatabaseRepository.saveTask(taskData)
        }
    }

    fun updateNote(noteData: NoteData) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseDatabaseRepository.updateNote(noteData)
        }

    }

    fun updateTask(taskData: TaskData) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseDatabaseRepository.updateTask(taskData)
        }

    }

    fun getNoteById(noteId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseDatabaseRepository.getNoteById(noteId).collect { responseData ->
                withContext(Dispatchers.Main) {
                    when (responseData) {
                        is ResponseData.Success -> {
                            _noteById.emit(responseData.result)
                        }
                        is ResponseData.Error -> {
                            _errorReceiving.emit(responseData.error)
                        }
                    }
                }
            }
        }
    }

    fun getTaskById(taskId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseDatabaseRepository.getTaskById(taskId).collect { responseData ->
                withContext(Dispatchers.Main) {
                    when (responseData) {
                        is ResponseData.Success -> {
                            _taskById.emit(responseData.result)
                        }
                        is ResponseData.Error -> {
                            _errorReceiving.emit(responseData.error)
                        }
                    }
                }
            }
        }
    }

    private fun fillNoteSearchKeywordsList(titleNote: String, contentNote: String): List<String> {
        val searchKeywordsList: MutableList<String> = mutableListOf()
        searchKeywordsList.add(titleNote.lowercase(Locale.getDefault()))
        searchKeywordsList.addAll(
            titleNote.lowercase(Locale.getDefault()).split(Regex("[\\p{Punct}\\s]+"))
        )

        searchKeywordsList.add(contentNote.lowercase(Locale.getDefault()))
        searchKeywordsList.addAll(
            contentNote.lowercase(Locale.getDefault()).split(Regex("[\\p{Punct}\\s]+"))
        )
        return searchKeywordsList
    }

    fun createNotification(taskData: TaskData) {
        notificationAlarmHelper.createNotificationAlarm(taskData)
    }

    fun cancelNotification(taskData: TaskData) {
        notificationAlarmHelper.cancelNotificationAlarm(taskData)
    }
}
