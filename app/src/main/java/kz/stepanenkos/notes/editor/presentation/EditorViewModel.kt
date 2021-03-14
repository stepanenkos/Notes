package kz.stepanenkos.notes.editor.presentation

import android.text.Spanned
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kz.stepanenkos.notes.NoteData
import kz.stepanenkos.notes.common.firebasedatabase.domain.FirebaseDatabaseRepository
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class EditorViewModel(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository
) : ViewModel() {
    private val _noteById: MutableLiveData<NoteData> = MutableLiveData()
    private val _onBold: MutableLiveData<Spanned> = MutableLiveData()
    private var fromHtml: Spanned? = null
    private var toHtml: String? = null
    val noteById: LiveData<NoteData> = _noteById
    val onBold: LiveData<Spanned> = _onBold

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
            firebaseDatabaseRepository.getNoteById(noteId).collect {
                withContext(Dispatchers.Main) { _noteById.value = it }
            }
        }
    }

    private suspend fun saveNoteToFirebaseDatabase(noteData: NoteData) {
        firebaseDatabaseRepository.saveNote(noteData)
    }

}
