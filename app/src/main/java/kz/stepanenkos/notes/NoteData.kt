package kz.stepanenkos.notes

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "notes")
data class NoteData @JvmOverloads constructor(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var titleNote: String = "",
    var contentNote: String = "",
/*    var dateOfNote: ZonedDateTime = ZonedDateTime.now(
        ZoneId.of(
            ZoneId.systemDefault().rules.getOffset(
                Instant.now()
            ).toString()
        )
    )*/
    var dateOfNote: String = ""
)