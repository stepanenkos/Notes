package kz.stepanenkos.notes.common.roomdatabase.domain

import kotlinx.coroutines.flow.Flow
import kz.stepanenkos.notes.NoteData

interface DatabaseRepository {
    fun getAllNotes(): Flow<List<NoteData>>
    fun getNoteById(noteId: String): Flow<NoteData>
    fun deleteNote(noteData: NoteData)
    fun deleteAllNotes()
    fun updateNote(noteData: NoteData)
    fun saveNote(noteData: NoteData)
    fun saveAllNotes(listNoteData: List<NoteData>)
    fun fillRoomDatabaseFromFirebaseDatabase(listNoteData: List<NoteData>)
}