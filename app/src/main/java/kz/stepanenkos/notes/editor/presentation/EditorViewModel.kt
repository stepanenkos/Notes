package kz.stepanenkos.notes.editor.presentation

import android.text.Spanned
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kz.stepanenkos.notes.NoteData
import kz.stepanenkos.notes.common.roomdatabase.domain.DatabaseRepository
import kz.stepanenkos.notes.common.firebasedatabase.domain.FirebaseDatabaseRepository
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class EditorViewModel(
    private val databaseRepository: DatabaseRepository,
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository
) : ViewModel() {
    private val _noteById: MutableLiveData<NoteData> = MutableLiveData()
    private val _onBold: MutableLiveData<Spanned> = MutableLiveData()
    private var fromHtml: Spanned? = null
    private var toHtml: String? = null
    val noteById: LiveData<NoteData> = _noteById
    val onBold: LiveData<Spanned> = _onBold

    fun saveNote(titleNote: String, contentNote: String) {
        val noteData = NoteData(
            titleNote = titleNote,
            contentNote = contentNote,
            dateOfNote = ZonedDateTime.now(
                ZoneId.of(
                    ZoneId.systemDefault().rules.getOffset(
                        Instant.now()
                    ).toString()
                )
            ).toEpochSecond()
        )
        saveNoteToRoomDatabase(noteData)
        saveNoteToFirebaseDatabase(noteData)
    }

    fun saveAllNotes(listNoteData: List<NoteData>) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.saveAllNotes(listNoteData)
        }
    }

    fun updateNote(noteData: NoteData) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.updateNote(noteData)
        }
        updateNoteInFirebaseDatabase(noteData)
    }

    fun getNoteById(noteId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.getNoteById(noteId).collect {
                withContext(Dispatchers.Main) { _noteById.value = it }
            }
        }
    }

    private fun saveNoteToRoomDatabase(noteData: NoteData) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.saveNote(noteData)
        }
    }

    private fun saveNoteToFirebaseDatabase(noteData: NoteData) {
        firebaseDatabaseRepository.saveNote(noteData)
    }

    private fun updateNoteInFirebaseDatabase(noteData: NoteData) {
        firebaseDatabaseRepository.updateNote(noteData)
    }
}
