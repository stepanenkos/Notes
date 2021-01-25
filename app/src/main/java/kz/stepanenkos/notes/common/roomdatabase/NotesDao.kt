package kz.stepanenkos.notes.common.roomdatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kz.stepanenkos.notes.NoteData

@Dao
interface NotesDao {
    @Query("SELECT * FROM notes")
    fun getAllNotes(): Flow<List<NoteData>>

    @Query("SELECT * FROM notes WHERE id=(:noteId)")
    fun getNoteById(noteId: String): Flow<NoteData>

    @Update
    fun updateNote(noteData: NoteData)

    @Insert
    fun saveNote(noteData: NoteData)

    @Insert
    fun saveAllNotes(listNoteData: List<NoteData>)

    @Insert
    fun fillRoomDatabaseFromFirebaseDatabase(listNoteData: List<NoteData>)

    @Delete
    fun deleteNote(noteData: NoteData)

    @Query("DELETE FROM notes")
    fun deleteAllNotes()
}