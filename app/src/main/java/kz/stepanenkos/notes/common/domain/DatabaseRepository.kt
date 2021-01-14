package kz.stepanenkos.notes.common.domain

import kotlinx.coroutines.flow.Flow
import kz.stepanenkos.notes.NoteData

interface DatabaseRepository {
    fun getAllNotes(): Flow<List<NoteData>>
    fun getNoteById(noteId: Long): Flow<NoteData>
    fun deleteNote(noteData: NoteData)
    fun updateNote(noteData: NoteData)
    fun addNote(noteData: NoteData)
}