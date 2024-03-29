package kz.stepanenkos.notes.common.firebasedatabase.data.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kz.stepanenkos.notes.common.model.NoteData
import kz.stepanenkos.notes.common.model.ResponseData
import kz.stepanenkos.notes.common.model.TaskData
import java.util.*

private const val USERS_NODE = "users"
private const val NOTES_NODE_CHILD = "notes"
private const val TASKS_NODE_CHILD = "tasks"

class DefaultFirebaseDatabaseSource(
    private val firebaseDatabase: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : FirebaseDatabaseSource {
    private val usersNode = firebaseDatabase.collection(USERS_NODE)


    override suspend fun saveNote(noteData: NoteData) {
        auth.currentUser?.uid?.let { uid ->
            usersNode
                .document(uid)
                .collection(NOTES_NODE_CHILD)
                .document(noteData.id.toString())
                .set(noteData)
        }
    }

    override suspend fun saveTask(taskData: TaskData) {
        auth.currentUser?.uid?.let { uid ->
            usersNode
                .document(uid)
                .collection(TASKS_NODE_CHILD)
                .document(taskData.id.toString())
                .set(taskData)
        }
    }

    @ExperimentalCoroutinesApi
    override suspend fun getNoteById(noteId: Int) =
        callbackFlow<ResponseData<NoteData, FirebaseFirestoreException>> {
            val noteById = auth.currentUser?.uid?.let { uid ->
                usersNode.document(uid).collection(NOTES_NODE_CHILD)
                    .addSnapshotListener { value, error ->
                        if (value != null) {
                            for (doc in value) {
                                if (doc.toObject<NoteData>().id == noteId) {
                                    trySend(ResponseData.Success(doc.toObject())).isSuccess
                                }
                            }
                        }
                        if (error != null) {
                            trySend(ResponseData.Error(error)).isFailure
                            cancel(error.localizedMessage!!)
                        }
                    }
            }
            awaitClose { noteById?.remove() }
        }


    @ExperimentalCoroutinesApi
    override suspend fun getTaskById(taskId: Int) =
        callbackFlow<ResponseData<TaskData, FirebaseFirestoreException>> {
            val taskById = auth.currentUser?.uid?.let { uid ->
                usersNode.document(uid).collection(TASKS_NODE_CHILD)
                    .addSnapshotListener { value, error ->
                        if (value != null) {
                            for (doc in value) {
                                if (doc.toObject(TaskData::class.java).id == taskId) {
                                    trySend(ResponseData.Success(doc.toObject())).isSuccess
                                }
                            }
                        }
                        if (error != null) {
                            trySend(ResponseData.Error(error)).isFailure
                            cancel(error.localizedMessage!!)
                        }
                    }
            }
            awaitClose { taskById?.remove() }
        }

    @ExperimentalCoroutinesApi
    override suspend fun getAllNotes() = callbackFlow<ResponseData<List<NoteData>, FirebaseFirestoreException>> {
        val getAllNotes = auth.currentUser?.uid?.let { uid ->
            usersNode.document(uid).collection(NOTES_NODE_CHILD)
                .addSnapshotListener { value, error ->
                    error?.let {
                        cancel(it.message.toString())
                    }
                    val listNoteData: MutableList<NoteData> = mutableListOf()
                    if (value != null) {
                        for (doc in value) {
                            listNoteData.add(doc.toObject<NoteData>())
                        }
                        listNoteData.sortByDescending { it.dateOfNote }
                        trySend(ResponseData.Success(listNoteData)).isSuccess
                    }
                    if (error != null) {
                        trySend(ResponseData.Error(error)).isFailure
                        cancel(error.localizedMessage!!)
                    }
                }

        }
        awaitClose { getAllNotes?.remove() }
    }

    @ExperimentalCoroutinesApi
    override suspend fun getAllTasks() = callbackFlow<ResponseData<List<TaskData>, FirebaseFirestoreException>> {
        val getAllTasks = auth.currentUser?.uid?.let { uid ->
            usersNode.document(uid).collection(TASKS_NODE_CHILD)
                .addSnapshotListener { value, error ->
                    error?.let {
                        cancel(it.message.toString())
                    }
                    val listTaskData: MutableList<TaskData> = mutableListOf()
                    if (value != null) {
                        for (doc in value) {
                            listTaskData.add(doc.toObject())
                        }
                        listTaskData.sortByDescending { it.dateOfTask }
                        trySend(ResponseData.Success(listTaskData)).isSuccess
                    }
                    if (error != null) {
                        trySend(ResponseData.Error(error)).isFailure
                        cancel(error.localizedMessage!!)
                    }
                }

        }
        awaitClose { getAllTasks?.remove() }
    }

    @ExperimentalCoroutinesApi
    override suspend fun searchNoteByText(searchKeyword: String) = callbackFlow<ResponseData<List<NoteData>, FirebaseFirestoreException>> {
        val searchNoteByText = auth.currentUser?.uid?.let { uid ->
            usersNode.document(uid).collection(NOTES_NODE_CHILD)
                .addSnapshotListener { value, error ->
                    val foundNotesBySearchText: MutableList<NoteData> = mutableListOf()
                    if (value != null) {
                        for (doc in value) {
                            if (doc.toObject(NoteData::class.java).titleNote.lowercase(Locale.ROOT)
                                    .contains(searchKeyword.lowercase(Locale.ROOT)) ||
                                doc.toObject(NoteData::class.java).contentNote.contains(
                                    searchKeyword.lowercase(Locale.ROOT)
                                )
                            ) {
                                foundNotesBySearchText.add(doc.toObject(NoteData::class.java))
                            }
                            trySend(ResponseData.Success(foundNotesBySearchText)).isSuccess
                        }

                    }
                    if(error != null) {
                        trySend(ResponseData.Error(error)).isFailure
                    }

                }
        }
        awaitClose { searchNoteByText?.remove() }
    }

    @ExperimentalCoroutinesApi
    override suspend fun searchTaskByText(searchKeyword: String) = callbackFlow<ResponseData<List<TaskData>, FirebaseFirestoreException>> {
        val searchTasksByText = auth.currentUser?.uid?.let { uid ->
            usersNode.document(uid).collection(TASKS_NODE_CHILD)
                .addSnapshotListener { value, error ->
                    val foundTasksBySearchText: MutableList<TaskData> = mutableListOf()
                    if (value != null) {
                        for (doc in value) {
                            if (doc.toObject(TaskData::class.java).contentTask.lowercase(Locale.ROOT)
                                    .contains(searchKeyword.lowercase(Locale.ROOT))
                            ) {
                                foundTasksBySearchText.add(doc.toObject(TaskData::class.java))
                            }
                        }
                        trySend(ResponseData.Success(foundTasksBySearchText)).isSuccess
                    }
                    if(error != null) {
                        trySend(ResponseData.Error(error)).isFailure
                    }
                }
        }
        awaitClose { searchTasksByText?.remove() }
    }

    override fun updateNote(noteData: NoteData) {
        auth.currentUser?.uid?.let { uid ->
            usersNode.document(uid)
                .collection(NOTES_NODE_CHILD)
                .document(noteData.id.toString())
                .update(
                    mapOf(
                        "titleNote" to noteData.titleNote,
                        "contentNote" to noteData.contentNote,
                        "dateOfNote" to noteData.dateOfNote,
                        "searchKeywords" to noteData.searchKeywords
                    )
                )
        }
    }

    override fun updateTask(taskData: TaskData) {
        auth.currentUser?.uid?.let { uid ->
            usersNode.document(uid)
                .collection(TASKS_NODE_CHILD)
                .document(taskData.id.toString())
                .update(
                    mapOf(
                        "contentTask" to taskData.contentTask,
                        "dateOfTask" to taskData.dateOfTask,
                        "notificationOn" to taskData.notificationOn,
                        "dateOfNotification" to taskData.dateOfNotification,
                        "doneTask" to taskData.doneTask
                    )
                )
        }
    }

    override fun deleteNote(noteData: NoteData) {
        auth.currentUser?.uid?.let { uid ->
            usersNode.document(uid).collection(NOTES_NODE_CHILD).document(noteData.id.toString()).delete()
        }
    }

    override fun deleteTask(taskData: TaskData) {
        auth.currentUser?.uid?.let { uid ->
            usersNode.document(uid).collection(TASKS_NODE_CHILD).document(taskData.id.toString()).delete()
        }
    }

}