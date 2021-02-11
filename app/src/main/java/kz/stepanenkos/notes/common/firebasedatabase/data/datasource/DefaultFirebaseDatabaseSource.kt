package kz.stepanenkos.notes.common.firebasedatabase.data.datasource

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kz.stepanenkos.notes.NoteData

private const val USERS_NODE = "users"
private const val NOTES_NODE_CHILD = "notes"

class DefaultFirebaseDatabaseSource(
    private val firebaseDatabase: FirebaseDatabase,
    private val auth: FirebaseAuth
) : FirebaseDatabaseSource {
    private val usersNode = firebaseDatabase.getReference(USERS_NODE)

    override fun saveNote(noteData: NoteData) {
        auth.currentUser?.uid?.let {
            usersNode.child(it).child(NOTES_NODE_CHILD).child(noteData.id).setValue(noteData)
        }
    }

    override fun saveAllNotes(listNoteData: List<NoteData>) {
        auth.currentUser?.uid?.let {
            usersNode.child(it).child(NOTES_NODE_CHILD).setValue(listNoteData)
        }
    }

    @ExperimentalCoroutinesApi
    override suspend fun getNoteById(noteId: String) = callbackFlow<NoteData> {
        auth.currentUser?.uid?.let { it ->
            usersNode.child(it).child(NOTES_NODE_CHILD).child(noteId).get().addOnSuccessListener { noteData ->
                if(noteData.exists()) {
                    this@callbackFlow.sendBlocking(noteData.getValue(NoteData::class.java)!!)
                }
            }

        }
        awaitClose { cancel() }
    }

    @ExperimentalCoroutinesApi
    override suspend fun getAllNotes() = callbackFlow<List<NoteData>> {
        auth.currentUser?.uid?.let { it ->

            usersNode.child(it).child(NOTES_NODE_CHILD)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            CoroutineScope(Dispatchers.IO).launch {
                                val list: MutableList<NoteData> = mutableListOf()
                                snapshot.children.forEach { dataSnapshot ->
                                    list.add(dataSnapshot.getValue(NoteData::class.java)!!)
                                }
                                list.sortWith { o1, o2 -> -(o1.dateOfNote - o2.dateOfNote).toInt() }
                                this@callbackFlow.sendBlocking(list)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
        awaitClose { cancel() }
    }

    override fun updateNote(noteData: NoteData) {
        auth.currentUser?.uid?.let {
            //firebaseDatabase.goOnline()
            usersNode.child(it).child(NOTES_NODE_CHILD).updateChildren(
                mapOf(noteData.id to noteData)
            )
        }
    }

    override fun deleteNote(noteData: NoteData) {
        auth.currentUser?.uid?.let {
            //firebaseDatabase.goOnline()
            usersNode.child(it).child(NOTES_NODE_CHILD).child(noteData.id).removeValue()
        }
    }
}