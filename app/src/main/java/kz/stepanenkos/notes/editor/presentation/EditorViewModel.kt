package kz.stepanenkos.notes.editor.presentation

import android.text.SpannableString
import android.text.Spanned
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kz.stepanenkos.notes.NoteData
import kz.stepanenkos.notes.common.domain.DatabaseRepository
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class EditorViewModel(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    private val _noteById: MutableLiveData<NoteData> = MutableLiveData()
    private val _onBold: MutableLiveData<Spanned> = MutableLiveData()
    private var fromHtml: Spanned? = null
    private var toHtml: String? = null
    val noteById: LiveData<NoteData> = _noteById
    val onBold: LiveData<Spanned> = _onBold
    private var spannableString: SpannableString? = null
    private var oldStartIndex = 0
    private var oldEndIndex = 0
    fun addNote(titleNote: String, contentNote: String) {
        addNoteToDatabase(
            NoteData(
                titleNote = titleNote,
                contentNote = contentNote,
                dateOfNote = ZonedDateTime.now(
                    ZoneId.of(
                        ZoneId.systemDefault().rules.getOffset(
                            Instant.now()
                        ).toString()
                    )
                )
            )
        )
    }

    private fun addNoteToDatabase(noteData: NoteData) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.addNote(noteData)
        }
    }

    fun updateNote(noteData: NoteData) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.updateNote(noteData)
        }
    }

    fun getNoteById(noteId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.getNoteById(noteId).collect {
                withContext(Dispatchers.Main) { _noteById.value = it }
            }
        }
    }

}