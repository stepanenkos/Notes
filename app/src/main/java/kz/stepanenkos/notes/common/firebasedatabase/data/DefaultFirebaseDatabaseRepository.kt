package kz.stepanenkos.notes.common.firebasedatabase.data

import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.Flow
import kz.stepanenkos.notes.common.firebasedatabase.data.datasource.FirebaseDatabaseSource
import kz.stepanenkos.notes.common.firebasedatabase.domain.FirebaseDatabaseRepository
import kz.stepanenkos.notes.common.model.NoteData
import kz.stepanenkos.notes.common.model.ResponseData
import kz.stepanenkos.notes.common.model.TaskData

class DefaultFirebaseDatabaseRepository(
    private val firebaseDatabaseSource: FirebaseDatabaseSource
) : FirebaseDatabaseRepository {
    override suspend fun saveNote(noteData: NoteData) {
        firebaseDatabaseSource.saveNote(noteData)
    }

    override suspend fun saveTask(taskData: TaskData) {
        firebaseDatabaseSource.saveTask(taskData)
    }

    override suspend fun getNoteById(noteId: Int): Flow<ResponseData<NoteData, FirebaseFirestoreException>> {
        return firebaseDatabaseSource.getNoteById(noteId)
    }
    override suspend fun getTaskById(taskId: Int): Flow<ResponseData<TaskData, FirebaseFirestoreException>> {
        return firebaseDatabaseSource.getTaskById(taskId)
    }


    override suspend fun getAllNotes(): Flow<ResponseData<List<NoteData>, FirebaseFirestoreException>> {
        return firebaseDatabaseSource.getAllNotes()
    }

    override suspend fun getAllTasks() : Flow<ResponseData<List<TaskData>, FirebaseFirestoreException>> {
        return firebaseDatabaseSource.getAllTasks()
    }


    override suspend fun searchNoteByText(searchKeyword: String): Flow<ResponseData<List<NoteData>, FirebaseFirestoreException>> {
        return firebaseDatabaseSource.searchNoteByText(searchKeyword)
    }

    override suspend fun searchTaskByText(searchKeyword: String): Flow<ResponseData<List<TaskData>, FirebaseFirestoreException>> {
        return firebaseDatabaseSource.searchTaskByText(searchKeyword)
    }

    override fun updateNote(noteData: NoteData) {
        firebaseDatabaseSource.updateNote(noteData)
    }

    override fun updateTask(taskData: TaskData) {
        firebaseDatabaseSource.updateTask(taskData)
    }

    override fun deleteNote(noteData: NoteData) {
        firebaseDatabaseSource.deleteNote(noteData)
    }

    override fun deleteTask(taskData: TaskData) {
        firebaseDatabaseSource.deleteTask(taskData)
    }
}

