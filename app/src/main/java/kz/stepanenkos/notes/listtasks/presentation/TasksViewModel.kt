package kz.stepanenkos.notes.listtasks.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kz.stepanenkos.notes.common.model.TaskData
import kz.stepanenkos.notes.common.firebasedatabase.domain.FirebaseDatabaseRepository
import kz.stepanenkos.notes.common.model.ResponseData
import kz.stepanenkos.notes.user.data.datasource.UserCredentialsDataSource

class TasksViewModel(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository,
) : ViewModel() {
    private val _allNotesFromDB: MutableLiveData<List<TaskData>> = MutableLiveData()
    val allNotes: LiveData<List<TaskData>> = _allNotesFromDB

    private val _errorWhileGettingNotes: MutableLiveData<FirebaseFirestoreException> = MutableLiveData()
    val errorWhileGettingNotes: LiveData<FirebaseFirestoreException> = _errorWhileGettingNotes

    fun onStart() {
        getAllTasks()
    }

    private fun getAllTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseDatabaseRepository.getAllTasks().collect { listTasksDataFromFirebaseDB ->
                withContext(Dispatchers.Main) {
                    when(listTasksDataFromFirebaseDB) {
                        is ResponseData.Success -> _allNotesFromDB.postValue(listTasksDataFromFirebaseDB.result)
                        is ResponseData.Error -> _errorWhileGettingNotes.postValue(listTasksDataFromFirebaseDB.error)
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