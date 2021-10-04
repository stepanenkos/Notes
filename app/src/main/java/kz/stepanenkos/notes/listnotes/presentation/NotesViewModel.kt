package kz.stepanenkos.notes.listnotes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kz.stepanenkos.notes.common.firebasedatabase.domain.FirebaseDatabaseRepository
import kz.stepanenkos.notes.common.model.NoteData
import kz.stepanenkos.notes.common.model.ResponseData

class NotesViewModel(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository,
) : ViewModel() {
    private val _allNotesFromDB: MutableSharedFlow<List<NoteData>> = MutableSharedFlow(replay = 1)
    val allNotes: SharedFlow<List<NoteData>> = _allNotesFromDB.asSharedFlow()

    private val _errorWhileGettingNotes: MutableSharedFlow<FirebaseFirestoreException> =
        MutableStateFlow(FirebaseFirestoreException(" ", FirebaseFirestoreException.Code.UNKNOWN))
    val errorWhileGettingNotes: SharedFlow<FirebaseFirestoreException> =
        _errorWhileGettingNotes.asSharedFlow()

    fun onStart() {
        getAllNotes()
    }

    private fun getAllNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseDatabaseRepository.getAllNotes().collect { listNoteDataFromFirebaseDB ->
                when (listNoteDataFromFirebaseDB) {
                    is ResponseData.Success -> _allNotesFromDB.tryEmit(listNoteDataFromFirebaseDB.result)

                    is ResponseData.Error -> _errorWhileGettingNotes.tryEmit(listNoteDataFromFirebaseDB.error)

                }
            }
        }
    }

    fun deleteNote(noteData: NoteData) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseDatabaseRepository.deleteNote(noteData)
        }
    }

    fun deleteNote(listNoteData: List<NoteData>) {
        for (noteData in listNoteData) {
            deleteNote(noteData)
        }
    }
}