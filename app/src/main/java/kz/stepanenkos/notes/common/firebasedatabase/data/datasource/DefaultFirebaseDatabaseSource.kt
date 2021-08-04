package kz.stepanenkos.notes.common.firebasedatabase.data.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import java.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kz.stepanenkos.notes.common.model.NoteData
import kz.stepanenkos.notes.common.model.TaskData
import kz.stepanenkos.notes.common.model.ResponseData

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
                .document(noteData.id)
                .set(noteData)
        }
    }

    override suspend fun saveTask(taskData: TaskData) {
        auth.currentUser?.uid?.let { uid ->
            usersNode
                .document(uid)
                .collection(TASKS_NODE_CHILD)
                .document(taskData.id)
                .set(taskData)
        }
    }

    @ExperimentalCoroutinesApi
    override suspend fun getNoteById(noteId: String) =
        callbackFlow<ResponseData<NoteData, FirebaseFirestoreException>> {
            val noteById = auth.currentUser?.uid?.let { uid ->
                usersNode.document(uid).collection(NOTES_NODE_CHILD)
                    .addSnapshotListener { value, error ->
                        if (value != null) {
                            for (doc in value) {
                                if (doc.toObject(NoteData::class.java).id == noteId) {
                                    offer(ResponseData.Success(doc.toObject(NoteData::class.java)))
                                }
                            }
                        }
                        if (error != null) {
                            trySend(ResponseData.Error(error))
                            cancel(error.localizedMessage!!)
                        }
                    }
            }
            awaitClose { noteById?.remove() }
        }

    @ExperimentalCoroutinesApi
    override suspend fun getTaskById(taskId: String) =
        callbackFlow<ResponseData<TaskData, FirebaseFirestoreException>> {
            val taskById = auth.currentUser?.uid?.let { uid ->
                usersNode.document(uid).collection(TASKS_NODE_CHILD)
                    .addSnapshotListener { value, error ->
                        if (value != null) {
                            for (doc in value) {
                                if (doc.toObject(TaskData::class.java).id == taskId) {
                                    trySend(ResponseData.Success(doc.toObject(TaskData::class.java)))
                                }
                            }
                        }
                        if (error != null) {
                            trySend(ResponseData.Error(error))
                            cancel(error.localizedMessage!!)
                        }
                    }
            }
            awaitClose { taskById?.remove() }
        }

    @ExperimentalCoroutinesApi
    override suspend fun getAllNotes() = callbackFlow {
        val getAllNotes = auth.currentUser?.uid?.let { uid ->
            usersNode.document(uid).collection(NOTES_NODE_CHILD)
                .addSnapshotListener { value, error ->
                    error?.let {
                        cancel(it.message.toString())
                    }
                    val listNoteData: MutableList<NoteData> = mutableListOf()
                    if (value != null) {
                        for (doc in value) {
                            listNoteData.add(doc.toObject(NoteData::class.java))
                        }
                        listNoteData.sortByDescending { it.dateOfNote }
                        offer(ResponseData.Success(listNoteData))
                    }
                    if (error != null) {
                        offer(ResponseData.Error(error))
                        cancel(error.localizedMessage!!)
                    }
                }

        }
        awaitClose { getAllNotes?.remove() }
    }

    @ExperimentalCoroutinesApi
    override suspend fun getAllTasks() = callbackFlow {
        val getAllTasks = auth.currentUser?.uid?.let { uid ->
            usersNode.document(uid).collection(TASKS_NODE_CHILD)
                .addSnapshotListener { value, error ->
                    error?.let {
                        cancel(it.message.toString())
                    }
                    val listTaskData: MutableList<TaskData> = mutableListOf()
                    if (value != null) {
                        for (doc in value) {
                            listTaskData.add(doc.toObject(TaskData::class.java))
                        }
                        listTaskData.sortByDescending { it.dateOfTask }
                        trySend(ResponseData.Success(listTaskData))
                    }
                    if (error != null) {
                        trySend(ResponseData.Error(error))
                        cancel(error.localizedMessage!!)
                    }
                }

        }
        awaitClose { getAllTasks?.remove() }
    }

    @ExperimentalCoroutinesApi
    override suspend fun searchNoteByText(searchKeyword: String) = callbackFlow<List<NoteData>> {
        val searchNoteByText = auth.currentUser?.uid?.let { uid ->
            usersNode.document(uid).collection(NOTES_NODE_CHILD)
                .addSnapshotListener { value, error ->
                    val foundNotesBySearchText: MutableList<NoteData> = mutableListOf()
                    if (value != null) {
                        for (doc in value) {
                            if (doc.toObject(NoteData::class.java).titleNote.toLowerCase(Locale.ROOT)
                                    .contains(searchKeyword.toLowerCase(Locale.ROOT)) ||
                                doc.toObject(NoteData::class.java).contentNote.contains(
                                    searchKeyword.toLowerCase(Locale.ROOT)
                                )
                            ) {
                                foundNotesBySearchText.add(doc.toObject(NoteData::class.java))
                            }
                        }
                    }
                    offer(foundNotesBySearchText)
                }
        }
        awaitClose { searchNoteByText?.remove() }
    }

    @ExperimentalCoroutinesApi
    override suspend fun searchTaskByText(searchKeyword: String) = callbackFlow<List<TaskData>> {
        val searchTasksByText = auth.currentUser?.uid?.let { uid ->
            usersNode.document(uid).collection(TASKS_NODE_CHILD)
                .addSnapshotListener { value, error ->
                    val foundTasksBySearchText: MutableList<TaskData> = mutableListOf()
                    if (value != null) {
                        for (doc in value) {
                            if (doc.toObject(TaskData::class.java).contentTask.toLowerCase(Locale.ROOT)
                                    .contains(searchKeyword.toLowerCase(Locale.ROOT))
                            ) {
                                foundTasksBySearchText.add(doc.toObject(TaskData::class.java))
                            }
                        }
                    }
                    trySend(foundTasksBySearchText)
                }
        }
        awaitClose { searchTasksByText?.remove() }
    }

    override fun updateNote(noteData: NoteData) {
        auth.currentUser?.uid?.let { uid ->
            usersNode.document(uid)
                .collection(NOTES_NODE_CHILD)
                .document(noteData.id)
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
                .document(taskData.id)
                .update(
                    mapOf(
                        "contentTask" to taskData.contentTask,
                        "dateOfTask" to taskData.dateOfTask,
                        "isNotification" to taskData.isNotification,
                        "dateOfNotification" to taskData.dateOfNotification,
                        "isDone" to taskData.isDone
                    )
                )
        }
    }

    override fun deleteNote(noteData: NoteData) {
        auth.currentUser?.uid?.let { uid ->
            usersNode.document(uid).collection(NOTES_NODE_CHILD).document(noteData.id).delete()
        }
    }

    override fun deleteTask(taskData: TaskData) {
        auth.currentUser?.uid?.let { uid ->
            usersNode.document(uid).collection(TASKS_NODE_CHILD).document(taskData.id).delete()
        }
    }

}