package kz.stepanenkos.notes.editor.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kz.stepanenkos.notes.common.model.NoteData
import kz.stepanenkos.notes.common.firebasedatabase.domain.FirebaseDatabaseRepository
import kz.stepanenkos.notes.common.model.ResponseData
import kz.stepanenkos.notes.common.model.TaskData
import java.util.*

class EditorViewModel(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository
) : ViewModel() {
    private val _noteById: MutableLiveData<NoteData> = MutableLiveData()
    val noteById: LiveData<NoteData> = _noteById

    private val _taskById: MutableLiveData<TaskData> = MutableLiveData()
    val taskById: LiveData<TaskData> = _taskById

    private val _errorReceiving: MutableLiveData<FirebaseFirestoreException> = MutableLiveData()
    val errorReceiving: LiveData<FirebaseFirestoreException> = _errorReceiving

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

    fun saveTask(contentTask: String, isNotificationOn: Boolean) {
        if (contentTask.isNotBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                val taskData = TaskData(
                    contentTask = contentTask,
                    notificationOn = isNotificationOn
                )
                saveTask(
                    taskData
                )
            }
        }

    }

    private suspend fun saveTask(taskData: TaskData) {
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

    fun getNoteById(noteId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseDatabaseRepository.getNoteById(noteId).collect { responseData ->
                withContext(Dispatchers.Main) {
                    when (responseData) {
                        is ResponseData.Success -> {
                            _noteById.postValue(responseData.result)
                        }
                        is ResponseData.Error -> {
                            _errorReceiving.postValue(responseData.error)
                        }
                    }
                }
            }
        }
    }

    fun getTaskById(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseDatabaseRepository.getTaskById(taskId).collect { responseData ->
                withContext(Dispatchers.Main) {
                    when (responseData) {
                        is ResponseData.Success -> {
                            _taskById.postValue(responseData.result)
                        }
                        is ResponseData.Error -> {
                            _errorReceiving.postValue(responseData.error)
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
}
