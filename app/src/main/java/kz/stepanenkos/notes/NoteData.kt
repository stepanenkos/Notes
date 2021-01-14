package kz.stepanenkos.notes

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

@Entity(tableName = "notes")
data class NoteData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var titleNote: String = "",
    var contentNote: String = "",
    var dateOfNote: ZonedDateTime = ZonedDateTime.now(
        ZoneId.of(
            ZoneId.systemDefault().rules.getOffset(
                Instant.now()
            ).toString()
        )
    )
)