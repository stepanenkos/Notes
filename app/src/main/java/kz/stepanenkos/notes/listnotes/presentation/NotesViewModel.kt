package kz.stepanenkos.notes.listnotes.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kz.stepanenkos.notes.NoteData
import kz.stepanenkos.notes.common.firebasedatabase.domain.FirebaseDatabaseRepository
import kz.stepanenkos.notes.user.data.datasource.UserCredentialsDataSource

class NotesViewModel(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository,
    private val userCredentialsDataSource: UserCredentialsDataSource,
) : ViewModel() {
    private var _allNotesFromDB: MutableLiveData<List<NoteData>> = MutableLiveData()
    var allNotes: LiveData<List<NoteData>> = _allNotesFromDB

    fun onStart() {
        getAllNotes()
    }

    private fun deleteAllNotes() {
        viewModelScope.launch(Dispatchers.IO) {
        }
    }

    private fun getAllNotes() {

                viewModelScope.launch(Dispatchers.IO) {
                    if (userCredentialsDataSource.getLastUserEmail().isNotBlank() &&
                        userCredentialsDataSource.getEmail().isNotBlank() &&
                        userCredentialsDataSource.getEmail() !=
                        userCredentialsDataSource.getLastUserEmail()
                    ) {
                        deleteAllNotes()
                    }

                    firebaseDatabaseRepository.getAllNotes().collect { listNoteDataFromFirebaseDB ->
                            withContext(Dispatchers.Main) {
                                _allNotesFromDB.postValue(listNoteDataFromFirebaseDB)
                            }
                        }
                    }
                }

    fun deleteNote(noteData: NoteData) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteNoteFromFirebaseDatabase(noteData)
        }
    }

    private fun deleteNoteFromFirebaseDatabase(noteData: NoteData) {
        firebaseDatabaseRepository.deleteNote(noteData)
    }

}