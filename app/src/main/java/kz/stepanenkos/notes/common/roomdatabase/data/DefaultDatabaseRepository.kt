package kz.stepanenkos.notes.common.roomdatabase.data

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kz.stepanenkos.notes.NoteData
import kz.stepanenkos.notes.common.roomdatabase.NotesDao
import kz.stepanenkos.notes.common.roomdatabase.domain.DatabaseRepository

class DefaultDatabaseRepository(
    private val notesDao: NotesDao
) : DatabaseRepository {
    override fun updateNote(noteData: NoteData) {
        notesDao.updateNote(noteData)
    }

    override fun saveNote(noteData: NoteData) {
        notesDao.saveNote(noteData)
    }

    override fun saveAllNotes(listNoteData: List<NoteData>) {
        notesDao.saveAllNotes(listNoteData)
    }

    override fun fillRoomDatabaseFromFirebaseDatabase(listNoteData: List<NoteData>) {
        notesDao.fillRoomDatabaseFromFirebaseDatabase(listNoteData)
    }

    override fun getAllNotes(): Flow<List<NoteData>> {
        return notesDao.getAllNotes()
    }

    override fun getNoteById(noteId: String): Flow<NoteData> {
        return notesDao.getNoteById(noteId)
    }

    override fun deleteNote(noteData: NoteData) {
        notesDao.deleteNote(noteData)
    }

    override fun deleteAllNotes() {
        notesDao.deleteAllNotes()
    }
}