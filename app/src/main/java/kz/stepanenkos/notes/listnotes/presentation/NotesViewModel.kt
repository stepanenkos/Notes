package kz.stepanenkos.notes.listnotes.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        when (auth.currentUser) {
            null -> {
                viewModelScope.launch(Dispatchers.IO) {
                    databaseRepository.getAllNotes().collect { listNoteDataFromRoomDB ->
                        _allNotesFromDB.postValue(listNoteDataFromRoomDB)
                    }
                }
            }

            else -> {
                viewModelScope.launch(Dispatchers.IO) {
                    if (userCredentialsDataSource.getLastUserEmail()
                            .isNotBlank() && userCredentialsDataSource.getEmail() != userCredentialsDataSource.getLastUserEmail()
                    ) {
                        deleteAllNotes()
                    }
                    databaseRepository.getAllNotes().collect { listNoteDataFromRoomDB ->
                        getAllNotesInFirebaseDatabase().collect { listNoteDataFromFirebaseDB ->
                            withContext(Dispatchers.Main) {
                                _allNotesFromDB.postValue(
                                    prepareListWithAllNotes(
                                        listNoteDataFromRoomDB,
                                        listNoteDataFromFirebaseDB
                                    )
                                )
                            }
                        }
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

    private suspend fun prepareListWithAllNotes(
        listFromRoomDB: List<NoteData>,
        listFromFirebaseDB: List<NoteData>
    ): List<NoteData> {
        val combinedList: MutableSet<NoteData> = mutableSetOf()
        val uniqueNotesForFirebase: MutableList<NoteData> = mutableListOf()
        val uniqueNotesForRoom: MutableList<NoteData> = mutableListOf()

        when {

            listFromRoomDB.isNotEmpty() && listFromRoomDB == listFromFirebaseDB -> {
                return listFromRoomDB
            }

            listFromRoomDB.size > listFromFirebaseDB.size || listFromRoomDB.size < listFromFirebaseDB.size -> {
                combinedList.addAll(listFromFirebaseDB)
                combinedList.addAll(listFromRoomDB)

                combinedList.forEach {
                    if (!listFromRoomDB.contains(it)) {
                        uniqueNotesForRoom.add(it)
                    }

                    if (!listFromFirebaseDB.contains(it)) {
                        uniqueNotesForFirebase.add(it)
                    }
                }

                withContext(Dispatchers.IO) {
                    databaseRepository.saveAllNotes(uniqueNotesForRoom)
                    firebaseDatabaseRepository.saveAllNotes(uniqueNotesForFirebase)
                }
                return combinedList.toList()
            }

            else -> {
                return listFromRoomDB
            }
        }
    }
}