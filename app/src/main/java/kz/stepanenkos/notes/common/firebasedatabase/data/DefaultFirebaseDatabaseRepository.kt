package kz.stepanenkos.notes.common.firebasedatabase.data

import kz.stepanenkos.notes.NoteData
import kz.stepanenkos.notes.common.firebasedatabase.data.datasource.FirebaseDatabaseSource
import kz.stepanenkos.notes.common.firebasedatabase.domain.FirebaseDatabaseRepository

class DefaultFirebaseDatabaseRepository(
    private val firebaseDatabaseSource: FirebaseDatabaseSource
) : FirebaseDatabaseRepository {
    override fun saveNote(noteData: NoteData) {
        firebaseDatabaseSource.saveNote(noteData)
    }

    override fun saveAllNotes(listNoteData: List<NoteData>) {
    }

    override suspend fun getAllNotes(): List<NoteData> {
        return firebaseDatabaseSource.getAllNotes()
    }

    override fun updateNote(noteData: NoteData) {
        firebaseDatabaseSource.updateNote(noteData)
    }

    override fun deleteNote(noteData: NoteData) {
        firebaseDatabaseSource.deleteNote(noteData)
    }
}