package kz.stepanenkos.notes.common.firebasedatabase.data.datasource

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import kz.stepanenkos.notes.NoteData

interface FirebaseDatabaseSource {
    fun saveNote(noteData: NoteData)

    fun saveAllNotes(listNoteData: List<NoteData>)

    suspend fun getAllNotes(): Flow<List<NoteData>>

    fun updateNote(noteData: NoteData)

    fun deleteNote(noteData: NoteData)
}