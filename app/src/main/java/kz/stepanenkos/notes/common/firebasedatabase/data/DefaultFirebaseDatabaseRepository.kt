package kz.stepanenkos.notes.common.firebasedatabase.data

import kotlinx.coroutines.flow.Flow
import kz.stepanenkos.notes.NoteData
import kz.stepanenkos.notes.common.firebasedatabase.data.datasource.FirebaseDatabaseSource
import kz.stepanenkos.notes.common.firebasedatabase.domain.FirebaseDatabaseRepository

class DefaultFirebaseDatabaseRepository(
    private val firebaseDatabaseSource: FirebaseDatabaseSource
) : FirebaseDatabaseRepository {
    override suspend fun saveNote(noteData: NoteData) {
        firebaseDatabaseSource.saveNote(noteData)
    }

    override suspend fun getNoteById(noteId: String) = firebaseDatabaseSource.getNoteById(noteId)


    override suspend fun getAllNotes() = firebaseDatabaseSource.getAllNotes()


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