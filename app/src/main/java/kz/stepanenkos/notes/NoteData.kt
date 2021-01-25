package kz.stepanenkos.notes

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "notes")
data class NoteData(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var titleNote: String = "",
    var contentNote: String = "",
    var dateOfNote: Long = 0
)