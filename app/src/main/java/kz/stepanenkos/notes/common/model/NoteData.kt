package kz.stepanenkos.notes.common.model


import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import kotlin.random.Random

@Parcelize
data class NoteData(
    val id: Int = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE),
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
