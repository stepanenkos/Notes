package kz.stepanenkos.notes.editor.presentation

import android.text.Spanned
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kz.stepanenkos.notes.NoteData
import kz.stepanenkos.notes.common.firebasedatabase.domain.FirebaseDatabaseRepository
import kz.stepanenkos.notes.common.model.ResponseData

class EditorViewModel(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository
) : ViewModel() {
    private val _noteById: MutableLiveData<NoteData> = MutableLiveData()
    val noteById: LiveData<NoteData> = _noteById

    private val _onBold: MutableLiveData<Spanned> = MutableLiveData()
    val onBold: LiveData<Spanned> = _onBold

    private val _errorReceivingNote: MutableLiveData<FirebaseFirestoreException> = MutableLiveData()
    val errorReceivingNote: LiveData<FirebaseFirestoreException> = _errorReceivingNote

    private var fromHtml: Spanned? = null
    private var toHtml: String? = null

    suspend fun saveNote(noteData: NoteData) {
        saveNoteToFirebaseDatabase(noteData)
    }

    fun updateNote(noteData: NoteData) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseDatabaseRepository.updateNote(noteData)
        }

    }

    fun getNoteById(noteId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseDatabaseRepository.getNoteById(noteId).collect {responseData ->
                withContext(Dispatchers.Main) {
                    when(responseData) {
                        is ResponseData.Success -> {
                            _noteById.postValue(responseData.result)
                        }
                        is ResponseData.Error -> {
                            _errorReceivingNote.postValue(responseData.error)
                        }
                    }
                }
            }
        }
    }

    private suspend fun saveNoteToFirebaseDatabase(noteData: NoteData) {
        firebaseDatabaseRepository.saveNote(noteData)
    }

}
