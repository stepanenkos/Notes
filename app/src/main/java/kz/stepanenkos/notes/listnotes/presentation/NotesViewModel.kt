package kz.stepanenkos.notes.listnotes.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kz.stepanenkos.notes.NoteData
import kz.stepanenkos.notes.common.roomdatabase.domain.DatabaseRepository
import kz.stepanenkos.notes.common.firebasedatabase.domain.FirebaseDatabaseRepository
import kz.stepanenkos.notes.user.data.datasource.UserCredentialsDataSource

class NotesViewModel(
    private val databaseRepository: DatabaseRepository,
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository,
    private val userCredentialsDataSource: UserCredentialsDataSource
) : ViewModel() {
    private var _allNotesFromDB: MutableLiveData<List<NoteData>> = MutableLiveData()
    var allNotes: LiveData<List<NoteData>> = _allNotesFromDB

    fun onStart() {
        getAllNotes()
    }

    private fun deleteAllNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.deleteAllNotes()
        }
    }

    private fun getAllNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            if (userCredentialsDataSource.getEmail() != userCredentialsDataSource.getLastUserEmail()) {
                deleteAllNotes()
            }
            databaseRepository.getAllNotes().collect { listNoteDataFromRoomDB ->
                if (listNoteDataFromRoomDB.isNotEmpty()) {
                    _allNotesFromDB.postValue(listNoteDataFromRoomDB.sortedWith{noteData1, noteData2 -> (noteData2.dateOfNote - noteData1.dateOfNote).toInt()})
                } else {
                    getAllNotesInFirebaseDatabase().collect { listNoteDataFromFirebaseDB ->
                        databaseRepository.fillRoomDatabaseFromFirebaseDatabase(
                            listNoteDataFromFirebaseDB
                        )
                        _allNotesFromDB.postValue(listNoteDataFromFirebaseDB)

                    }
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