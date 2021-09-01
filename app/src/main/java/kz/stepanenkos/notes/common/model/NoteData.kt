package kz.stepanenkos.notes.common.model


import android.os.Parcelable
import java.util.*
import kotlinx.parcelize.Parcelize
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

@Parcelize
data class NoteData(
    val id: String = UUID.randomUUID().toString(),
    val titleNote: String = "",
    val contentNote: String = "",
    val dateOfNote: Long = ZonedDateTime.now(
        ZoneId.of(
            ZoneId.systemDefault().rules.getOffset(
                Instant.now()
            ).toString()
        )
    ).toEpochSecond(),
    val searchKeywords: List<String> = mutableListOf()
) : Parcelable
