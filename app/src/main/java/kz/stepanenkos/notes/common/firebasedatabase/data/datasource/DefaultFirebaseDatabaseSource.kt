package kz.stepanenkos.notes.common.firebasedatabase.data.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.tasks.await
import kz.stepanenkos.notes.NoteData

private const val NOTES_NODE_CHILD = "notes"

class DefaultFirebaseDatabaseSource(
    private val dbReference: DatabaseReference,
    private val auth: FirebaseAuth
) : FirebaseDatabaseSource {
    override fun saveNote(noteData: NoteData) {
        auth.currentUser?.uid?.let {
            dbReference.child(it).child(NOTES_NODE_CHILD).child(noteData.id).setValue(noteData)
        }
    }

    override fun saveAllNotes(listNoteData: List<NoteData>) {
        auth.currentUser?.uid?.let {
            dbReference.child(it).child(NOTES_NODE_CHILD).setValue(listNoteData)
        }
    }

    override suspend fun getAllNotes(): List<NoteData> {
        val allNotes: MutableList<NoteData> = mutableListOf()
        auth.currentUser?.uid?.let {
            val await = dbReference.child(it).child(NOTES_NODE_CHILD).get().await()
            if (await.exists()) {
                await.children.forEach { dataS ->
                    allNotes.add(
                        dataS.getValue(NoteData::class.java)!!
                    )
                }
            }
        }
        return allNotes
    }

    override fun updateNote(noteData: NoteData) {
        auth.currentUser?.uid?.let {
            dbReference.child(it).child(NOTES_NODE_CHILD).updateChildren(
                mapOf(noteData.id to noteData)
            )
        }
    }

    override fun deleteNote(noteData: NoteData) {
        auth.currentUser?.uid?.let {
            dbReference.child(it).child(NOTES_NODE_CHILD).child(noteData.id).removeValue()
        }
    }
}