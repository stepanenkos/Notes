package kz.stepanenkos.notes.common.firebasedatabase.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kz.stepanenkos.notes.NoteData
import kz.stepanenkos.notes.common.firebasedatabase.data.datasource.FirebaseDatabaseSource
import kz.stepanenkos.notes.common.firebasedatabase.domain.FirebaseDatabaseRepository

class DefaultFirebaseDatabaseRepository(
    private val firebaseDatabaseSource: FirebaseDatabaseSource
) : FirebaseDatabaseRepository {
    override suspend fun saveNote(noteData: NoteData) {
        firebaseDatabaseSource.saveNote(noteData)
    }

    override suspend fun getNoteById(noteId: String): Flow<NoteData> {
        return firebaseDatabaseSource.getNoteById(noteId)
    }

    override suspend fun getAllNotes(): Flow<List<NoteData>> {
        return firebaseDatabaseSource.getAllNotes()
    }

    override suspend fun searchNoteByText(searchKeyword: String): Flow<List<NoteData>> {
        return firebaseDatabaseSource.searchNoteByText(searchKeyword)
    }

    override fun updateNote(noteData: NoteData) {
        firebaseDatabaseSource.updateNote(noteData)
    }

    override fun deleteNote(noteData: NoteData) {
        firebaseDatabaseSource.deleteNote(noteData)
    }
}