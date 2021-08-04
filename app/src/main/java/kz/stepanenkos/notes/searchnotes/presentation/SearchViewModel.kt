package kz.stepanenkos.notes.searchnotes.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kz.stepanenkos.notes.common.model.NoteData
import kz.stepanenkos.notes.common.firebasedatabase.domain.FirebaseDatabaseRepository

class SearchViewModel(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository,
) : ViewModel() {
    private val _allFoundNotes: MutableLiveData<List<NoteData>> = MutableLiveData()
    val allFoundNotes: LiveData<List<NoteData>> = _allFoundNotes
    suspend fun searchNoteByText(searchKeyword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseDatabaseRepository.searchNoteByText(searchKeyword).collect { allFoundNotes ->
                withContext(Dispatchers.Main) {
                    _allFoundNotes.postValue(allFoundNotes)
                }
            }
        }

    }
}