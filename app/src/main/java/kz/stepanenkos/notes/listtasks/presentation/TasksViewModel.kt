package kz.stepanenkos.notes.listtasks.presentation

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
import kz.stepanenkos.notes.common.model.ResponseData
import kz.stepanenkos.notes.common.model.TaskData

class TasksViewModel(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository,
) : ViewModel() {
    private val _allNotesFromDB: MutableSharedFlow<List<TaskData>> = MutableSharedFlow(replay = 1)
    val allNotes: SharedFlow<List<TaskData>> = _allNotesFromDB.asSharedFlow()

    private val _errorWhileGettingNotes: MutableSharedFlow<FirebaseFirestoreException> = MutableSharedFlow(replay = 1)
    val errorWhileGettingNotes: SharedFlow<FirebaseFirestoreException> = _errorWhileGettingNotes.asSharedFlow()

    fun onStart() {
        getAllTasks()
    }

    private fun getAllTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseDatabaseRepository.getAllTasks().collect { listTasksDataFromFirebaseDB ->
                withContext(Dispatchers.Main) {
                    when(listTasksDataFromFirebaseDB) {
                        is ResponseData.Success -> _allNotesFromDB.emit(listTasksDataFromFirebaseDB.result)
                        is ResponseData.Error -> _errorWhileGettingNotes.emit(listTasksDataFromFirebaseDB.error)
                    }

                }
            }
        }
    }

    fun deleteTask(taskData: TaskData) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseDatabaseRepository.deleteTask(taskData)
        }
    }

    fun updateTask(taskData: TaskData) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseDatabaseRepository.updateTask(taskData)
        }
    }

    fun saveTask(taskData: TaskData) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseDatabaseRepository.saveTask(taskData)
        }
    }
}