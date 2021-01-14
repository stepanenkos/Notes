package kz.stepanenkos.notes.common.roomdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kz.stepanenkos.notes.NoteData

@Database(entities = [NoteData::class], version = 1, exportSchema = false)
@TypeConverters(NotesTypeConverters::class)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun notesDao(): NotesDao

}