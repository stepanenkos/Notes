package kz.stepanenkos.notes.listnotes.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kz.stepanenkos.notes.NoteData
import kz.stepanenkos.notes.common.domain.DatabaseRepository

class NotesViewModel(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    private val _allNotes: MutableLiveData<List<NoteData>> = MutableLiveData()

    val allNotes: LiveData<List<NoteData>> = _allNotes

    fun onStart() {
        getAllNotes()
    }

    private fun getAllNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                databaseRepository.getAllNotes().collect {
                    _allNotes.value = it
                }
            }
        }
    }

    fun deleteNote(noteData: NoteData) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.deleteNote(noteData)
        }
    }
}