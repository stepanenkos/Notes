package kz.stepanenkos.notes.common.firebasedatabase.domain

import kz.stepanenkos.notes.NoteData

interface FirebaseDatabaseRepository {
    fun saveNote(noteData: NoteData)

    fun saveAllNotes(listNoteData: List<NoteData>)

    suspend fun getAllNotes(): List<NoteData>

    fun updateNote(noteData: NoteData)

    fun deleteNote(noteData: NoteData)
}