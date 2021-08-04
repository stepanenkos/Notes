package kz.stepanenkos.notes.editor.presentation

import android.text.Spanned
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

class EditorViewModel(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository
) : ViewModel() {
    private val _noteById: MutableLiveData<NoteData> = MutableLiveData()
    val noteById: LiveData<NoteData> = _noteById

    private val _taskById: MutableLiveData<TaskData> = MutableLiveData()
    val taskById: LiveData<TaskData> = _taskById

    private val _onBold: MutableLiveData<Spanned> = MutableLiveData()
    val onBold: LiveData<Spanned> = _onBold

    private val _errorReceiving: MutableLiveData<FirebaseFirestoreException> = MutableLiveData()
    val errorReceiving: LiveData<FirebaseFirestoreException> = _errorReceiving

    private var fromHtml: Spanned? = null
    private var toHtml: String? = null

    suspend fun saveNote(noteData: NoteData) {
        firebaseDatabaseRepository.saveNote(noteData)
    }

    suspend fun saveTask(taskData: TaskData) {
        firebaseDatabaseRepository.saveTask(taskData)
    }

    fun updateNote(noteData: NoteData) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseDatabaseRepository.updateNote(noteData)
        }

    }

    fun getNoteById(noteId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseDatabaseRepository.getNoteById(noteId).collect {responseData ->
                withContext(Dispatchers.Main) {
                    when(responseData) {
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
                    when(responseData) {
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
}
