package kz.stepanenkos.notes.listnotes.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kz.stepanenkos.notes.NoteData
import kz.stepanenkos.notes.common.domain.DatabaseRepository
import kz.stepanenkos.notes.common.firebasedatabase.domain.FirebaseDatabaseRepository

class NotesViewModel(
    private val databaseRepository: DatabaseRepository,
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository
) : ViewModel() {
    private var _allNotesFromDB: MutableLiveData<List<NoteData>> = MutableLiveData()
    var allNotes: LiveData<List<NoteData>> = _allNotesFromDB

    fun onStart() {
        getAllNotes()
    }

    private fun getAllNotes() {

        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.getAllNotes().collect {listNoteDataFromRoomDB ->
                if (listNoteDataFromRoomDB.isEmpty()) {
                    getAllNotesInFirebaseDatabase().collect {listNoteDataFromFirebaseDB ->
                        _allNotesFromDB.postValue(listNoteDataFromFirebaseDB)
                        if(listNoteDataFromFirebaseDB.size > listNoteDataFromRoomDB.size) {
                            databaseRepository.fillRoomDatabaseFromFirebaseDatabase(listNoteDataFromFirebaseDB)
                        }
                    }
                } else {
                    _allNotesFromDB.postValue(listNoteDataFromRoomDB)
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

    private suspend fun getAllNotesInFirebaseDatabase(): Flow<List<NoteData>> {
        return firebaseDatabaseRepository.getAllNotes()
    }
}