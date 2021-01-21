package kz.stepanenkos.notes.common.firebasedatabase.data.datasource

import kz.stepanenkos.notes.NoteData

interface FirebaseDatabaseSource {
    fun saveNote(noteData: NoteData)

    fun saveAllNotes(listNoteData: List<NoteData>)

    suspend fun getAllNotes(): List<NoteData>

    fun updateNote(noteData: NoteData)

    fun deleteNote(noteData: NoteData)
}