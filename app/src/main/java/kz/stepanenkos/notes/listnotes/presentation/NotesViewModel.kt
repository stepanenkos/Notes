package kz.stepanenkos.notes.listnotes.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kz.stepanenkos.notes.common.model.NoteData
import kz.stepanenkos.notes.common.firebasedatabase.domain.FirebaseDatabaseRepository
import kz.stepanenkos.notes.common.model.ResponseData
import kz.stepanenkos.notes.user.data.datasource.UserCredentialsDataSource

class NotesViewModel(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository,
    private val userCredentialsDataSource: UserCredentialsDataSource,
) : ViewModel() {
    private val _allNotesFromDB: MutableLiveData<List<NoteData>> = MutableLiveData()
    val allNotes: LiveData<List<NoteData>> = _allNotesFromDB

    private val _errorWhileGettingNotes: MutableLiveData<FirebaseFirestoreException> = MutableLiveData()
    val errorWhileGettingNotes: LiveData<FirebaseFirestoreException> = _errorWhileGettingNotes

    fun onStart() {
        getAllNotes()
    }

    private fun getAllNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseDatabaseRepository.getAllNotes().collect { listNoteDataFromFirebaseDB ->
                withContext(Dispatchers.Main) {
                    when(listNoteDataFromFirebaseDB) {
                        is ResponseData.Success -> _allNotesFromDB.postValue(listNoteDataFromFirebaseDB.result)
                        is ResponseData.Error -> _errorWhileGettingNotes.postValue(listNoteDataFromFirebaseDB.error)
                    }

                }
            }
        }
    }

    fun deleteNote(noteData: NoteData) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseDatabaseRepository.deleteNote(noteData)
        }
    }
}