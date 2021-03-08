package kz.stepanenkos.notes

import java.util.*
import kotlinx.serialization.Serializable
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

@Serializable
data class NoteData(
    var id: String = UUID.randomUUID().toString(),
    var titleNote: String = "",
    var contentNote: String = "",
    var dateOfNote: Long = ZonedDateTime.now(
        ZoneId.of(
            ZoneId.systemDefault().rules.getOffset(
                Instant.now()
            ).toString()
        )
    ).toEpochSecond()
)