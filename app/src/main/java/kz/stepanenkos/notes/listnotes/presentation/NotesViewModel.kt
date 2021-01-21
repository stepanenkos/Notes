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
import kz.stepanenkos.notes.common.firebasedatabase.domain.FirebaseDatabaseRepository

class NotesViewModel(
    private val databaseRepository: DatabaseRepository,
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository
) : ViewModel() {
    private val _allNotes: MutableLiveData<List<NoteData>> = MutableLiveData()

    val allNotes: LiveData<List<NoteData>> = _allNotes

    fun onStart() {
        getAllNotes()
    }

    private fun getAllNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            val allNotesInFirebaseDatabase: MutableList<NoteData> = getAllNotesInFirebaseDatabase() as MutableList<NoteData>
            databaseRepository.getAllNotes().collect {
                if (it.isEmpty() && allNotesInFirebaseDatabase.isNotEmpty()) {
                    _allNotes.postValue(allNotesInFirebaseDatabase)
                    databaseRepository.fillRoomDatabaseFromFirebaseDatabase(allNotesInFirebaseDatabase)
                } else {
                    _allNotes.postValue(it)
                    allNotesInFirebaseDatabase.clear()
                }
            }
        }
    }

    fun deleteNote(noteData: NoteData) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.deleteNote(noteData)
            deleteNoteFromFirebaseDatabase(noteData)
        }
    }

    private fun deleteNoteFromFirebaseDatabase(noteData: NoteData) {
        firebaseDatabaseRepository.deleteNote(noteData)
    }

    private suspend fun getAllNotesInFirebaseDatabase(): List<NoteData> {
        return firebaseDatabaseRepository.getAllNotes()
    }
}