package kz.stepanenkos.notes.common.firebasedatabase.data.datasource

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import kz.stepanenkos.notes.NoteData

interface FirebaseDatabaseSource {
    fun saveNote(noteData: NoteData)

    suspend fun getNoteById(noteId: String): Flow<NoteData>

    suspend fun getAllNotes(): Flow<List<NoteData>>

    suspend fun searchNoteByText(searchText: String): Flow<List<NoteData>>

    fun updateNote(noteData: NoteData)

    fun deleteNote(noteData: NoteData)
}