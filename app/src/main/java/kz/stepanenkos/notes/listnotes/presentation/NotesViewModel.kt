package kz.stepanenkos.notes.listnotes.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kz.stepanenkos.notes.NoteData
import kz.stepanenkos.notes.common.firebasedatabase.domain.FirebaseDatabaseRepository
import kz.stepanenkos.notes.common.roomdatabase.domain.DatabaseRepository
import kz.stepanenkos.notes.user.data.datasource.UserCredentialsDataSource

class NotesViewModel(
    private val databaseRepository: DatabaseRepository,
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository,
    private val userCredentialsDataSource: UserCredentialsDataSource,
    private val auth: FirebaseAuth
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
        if (auth.currentUser == null) {
            viewModelScope.launch(Dispatchers.IO) {
                databaseRepository.getAllNotes().collect { listNoteDataFromRoomDB ->
                    _allNotesFromDB.postValue(listNoteDataFromRoomDB)
                }
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                /*if (userCredentialsDataSource.getEmail() != userCredentialsDataSource.getLastUserEmail()) {
                    deleteAllNotes()
                }*/
                databaseRepository.getAllNotes().collect { listNoteDataFromRoomDB ->
                    getAllNotesInFirebaseDatabase().collect { listNoteDataFromFirebaseDB ->
                        _allNotesFromDB.postValue(prepareListWithAllNotes(listNoteDataFromRoomDB, listNoteDataFromFirebaseDB))
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

    private fun prepareListWithAllNotes(listFromRoomDB: List<NoteData>, listFromFirebaseDB: List<NoteData>): List<NoteData> {
        val combinedList: MutableList<NoteData> = mutableListOf()
        combinedList.clear()
        when {

            listFromRoomDB.isNotEmpty() && listFromRoomDB == listFromFirebaseDB -> {
                return listFromRoomDB
            }

            listFromRoomDB.size < listFromFirebaseDB.size -> {
                combinedList.addAll(listFromRoomDB)
                listFromFirebaseDB.forEach {
                    if(!combinedList.contains(it)) combinedList.add(it)
                }
                databaseRepository.fillRoomDatabaseFromFirebaseDatabase(
                    combinedList
                )
                return combinedList
            }

            listFromRoomDB.size > listFromFirebaseDB.size -> {
                combinedList.addAll(listFromFirebaseDB)
                listFromRoomDB.forEach {
                    if(!combinedList.contains(it)) {
                        combinedList.add(it)
                        firebaseDatabaseRepository.saveNote(it)
                    }
                }
                return combinedList
            }

            else -> {
                combinedList.addAll(listFromRoomDB)
                listFromFirebaseDB.forEach { if (!combinedList.contains(it)) combinedList.add(it) }
                return combinedList
            }
        }
    }
}