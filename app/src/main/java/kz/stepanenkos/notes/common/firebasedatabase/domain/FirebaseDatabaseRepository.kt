package kz.stepanenkos.notes.common.firebasedatabase.domain

import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.Flow
import kz.stepanenkos.notes.NoteData
import kz.stepanenkos.notes.common.model.ResponseData

interface FirebaseDatabaseRepository {
   suspend fun saveNote(noteData: NoteData)

    suspend fun getNoteById(noteId: String): Flow<ResponseData<NoteData, FirebaseFirestoreException>>

    suspend fun getAllNotes(): Flow<ResponseData<List<NoteData>, FirebaseFirestoreException>>

    suspend fun searchNoteByText(searchKeyword: String): Flow<List<NoteData>>

    fun updateNote(noteData: NoteData)

    fun deleteNote(noteData: NoteData)
}