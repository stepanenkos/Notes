package kz.stepanenkos.notes.common.model


import android.os.Parcelable
import java.util.*
import kotlinx.parcelize.Parcelize
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

@Parcelize
data class TaskData(
    val id: String = UUID.randomUUID().toString(),
    val contentTask: String = "",
    val dateOfTask: Long = ZonedDateTime.now(
        ZoneId.of(
            ZoneId.systemDefault().rules.getOffset(
                Instant.now()
            ).toString()
        )
    ).toEpochSecond(),
    val isNotification: Boolean = false,
    val dateOfNotification: Long = 0,
    val isDone: Boolean = false,
) : Parcelable
