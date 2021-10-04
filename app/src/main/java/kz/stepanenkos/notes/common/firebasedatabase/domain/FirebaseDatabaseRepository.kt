package kz.stepanenkos.notes.common.firebasedatabase.domain

import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.Flow
import kz.stepanenkos.notes.common.model.NoteData
import kz.stepanenkos.notes.common.model.TaskData
import kz.stepanenkos.notes.common.model.ResponseData

interface FirebaseDatabaseRepository {
   suspend fun saveNote(noteData: NoteData)
   suspend fun saveTask(taskData: TaskData)

    suspend fun getNoteById(noteId: Int): Flow<ResponseData<NoteData, FirebaseFirestoreException>>
    suspend fun getTaskById(taskId: Int): Flow<ResponseData<TaskData, FirebaseFirestoreException>>

    suspend fun getAllNotes(): Flow<ResponseData<List<NoteData>, FirebaseFirestoreException>>
    suspend fun getAllTasks(): Flow<ResponseData<List<TaskData>, FirebaseFirestoreException>>

    suspend fun searchNoteByText(searchKeyword: String): Flow<List<NoteData>>
    suspend fun searchTaskByText(searchKeyword: String): Flow<List<TaskData>>

    fun updateNote(noteData: NoteData)
    fun updateTask(taskData: TaskData)

    fun deleteNote(noteData: NoteData)
    fun deleteTask(taskData: TaskData)
}