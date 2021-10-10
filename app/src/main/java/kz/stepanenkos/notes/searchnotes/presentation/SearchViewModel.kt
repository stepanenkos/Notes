package kz.stepanenkos.notes.searchnotes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kz.stepanenkos.notes.common.firebasedatabase.domain.FirebaseDatabaseRepository
import kz.stepanenkos.notes.common.model.ResponseData
import kz.stepanenkos.notes.searchnotes.data.AllListItem

class SearchViewModel(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository,
) : ViewModel() {
    private val _allFoundNotes: MutableSharedFlow<List<AllListItem>> = MutableSharedFlow(replay = 1)
    private val _allFoundTasks: MutableSharedFlow<List<AllListItem>> = MutableSharedFlow(replay = 1)

    val allFoundItems: SharedFlow<List<AllListItem>> =
        _allFoundNotes.combine(_allFoundTasks) { foundNotes, foundTasks ->
            foundNotes + foundTasks
        }.shareIn(scope = viewModelScope, replay = 1, started = SharingStarted.WhileSubscribed())

    private val _searchError: MutableSharedFlow<FirebaseException> = MutableSharedFlow(replay = 1)

    val searchError: SharedFlow<FirebaseException> = _searchError.asSharedFlow()

    suspend fun searchNoteByText(searchKeyword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val allListItem = mutableListOf<AllListItem>()
            firebaseDatabaseRepository.searchNoteByText(searchKeyword).collect { foundNotes ->
                when (foundNotes) {
                    is ResponseData.Success -> {
                        foundNotes.result.forEach { noteData ->
                            allListItem.add(AllListItem.NotesListItem(noteData))
                        }
                        _allFoundNotes.emit(allListItem)
                    }
                    is ResponseData.Error -> {
                        _searchError.emit(foundNotes.error)
                    }
                }
            }
        }
    }

    suspend fun searchTaskByText(searchKeyword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val allListItem = mutableListOf<AllListItem>()
            firebaseDatabaseRepository.searchTaskByText(searchKeyword).collect { foundTasks ->
                when (foundTasks) {
                    is ResponseData.Success -> {
                        foundTasks.result.forEach { taskData ->
                            allListItem.add(AllListItem.TasksListItem(taskData))
                        }
                        _allFoundTasks.emit(allListItem)
                    }
                    is ResponseData.Error -> {
                        _searchError.emit(foundTasks.error)
                    }
                }
            }
        }
    }
}