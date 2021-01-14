package kz.stepanenkos.notes.common.data

import kotlinx.coroutines.flow.Flow
import kz.stepanenkos.notes.NoteData
import kz.stepanenkos.notes.common.roomdatabase.NotesDao
import kz.stepanenkos.notes.common.domain.DatabaseRepository

class DefaultDatabaseRepository(
    private val notesDao: NotesDao
) : DatabaseRepository{
    override fun updateNote(noteData: NoteData) {
        notesDao.updateNote(noteData)
    }

    override fun addNote(noteData: NoteData) {
        notesDao.addNote(noteData)
    }

    override fun getAllNotes(): Flow<List<NoteData>> {
        return notesDao.getAllNotes()
    }

    override fun getNoteById(noteId: Long): Flow<NoteData> {
        return notesDao.getNoteById(noteId)
    }

    override fun deleteNote(noteData: NoteData) {
        notesDao.deleteNote(noteData)
    }
}