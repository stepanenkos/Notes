package kz.stepanenkos.notes.common.firebasedatabase.data.datasource

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kz.stepanenkos.notes.NoteData

private const val USERS_NODE = "users"
private const val NOTES_NODE_CHILD = "notes"

class DefaultFirebaseDatabaseSource(
    private val firebaseDatabase: FirebaseFirestore,
    private val auth: FirebaseAuth
) : FirebaseDatabaseSource {
    private val usersNode = firebaseDatabase.collection(USERS_NODE)

    override fun saveNote(noteData: NoteData) {
        auth.currentUser?.uid?.let { uid ->
            usersNode
                .document(uid)
                .collection(NOTES_NODE_CHILD)
                .document(noteData.id)
                .set(noteData)
        }
    }

    @ExperimentalCoroutinesApi
    override suspend fun getNoteById(noteId: String) = callbackFlow<NoteData> {
        auth.currentUser?.uid?.let { uid ->
            usersNode.document(uid).collection(NOTES_NODE_CHILD).get()
                .addOnSuccessListener {
                        for (doc in it.documents) {
                            if (doc.toObject(NoteData::class.java)?.id == noteId) {
                                this@callbackFlow.sendBlocking(doc.toObject(NoteData::class.java)!!)
                            }
                        }
                }
        }
        awaitClose { cancel() }
    }

    @ExperimentalCoroutinesApi
    override suspend fun getAllNotes() = callbackFlow<List<NoteData>> {
        auth.currentUser?.uid?.let { uid ->
            usersNode.document(uid).collection(NOTES_NODE_CHILD).get().addOnSuccessListener {
                val listNoteData: MutableList<NoteData> = mutableListOf()
                for (doc in it) {
                    listNoteData.add(doc.toObject(NoteData::class.java))
                }
                this@callbackFlow.sendBlocking(listNoteData)
            }
        }
        awaitClose { cancel() }
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
                        "dateOfNote" to noteData.dateOfNote
                    )
                )
        }
    }

    override fun deleteNote(noteData: NoteData) {
        auth.currentUser?.uid?.let { uid ->
            usersNode.document(uid).collection(NOTES_NODE_CHILD).document(noteData.id).delete()
        }
    }
}