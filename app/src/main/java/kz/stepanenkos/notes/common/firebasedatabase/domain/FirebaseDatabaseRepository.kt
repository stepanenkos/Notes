package kz.stepanenkos.notes.common.firebasedatabase.domain

import kotlinx.coroutines.flow.Flow
import kz.stepanenkos.notes.NoteData

interface FirebaseDatabaseRepository {
   suspend fun saveNote(noteData: NoteData)

    suspend fun getNoteById(noteId: String): Flow<NoteData>

    suspend fun getAllNotes(): Flow<List<NoteData>>

    suspend fun searchNoteByText(searchKeyword: String): Flow<List<NoteData>>

    fun updateNote(noteData: NoteData)

    fun deleteNote(noteData: NoteData)
}