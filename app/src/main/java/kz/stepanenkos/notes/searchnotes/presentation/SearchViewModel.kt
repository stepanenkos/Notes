package kz.stepanenkos.notes.searchnotes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kz.stepanenkos.notes.common.firebasedatabase.domain.FirebaseDatabaseRepository
import kz.stepanenkos.notes.common.model.NoteData

class SearchViewModel(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository,
) : ViewModel() {
    private val _allFoundNotes: MutableSharedFlow<List<NoteData>> = MutableSharedFlow(replay = 1)
    val allFoundNotes: SharedFlow<List<NoteData>> = _allFoundNotes.asSharedFlow()
    suspend fun searchNoteByText(searchKeyword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseDatabaseRepository.searchNoteByText(searchKeyword).collect { allFoundNotes ->
                withContext(Dispatchers.Main) {
                    _allFoundNotes.emit(allFoundNotes)
                }
            }
        }

    }
}