package kz.stepanenkos.notes


import android.os.Parcelable
import java.util.*
import kotlinx.parcelize.Parcelize
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

@Parcelize
data class TaskData(
    var id: String = UUID.randomUUID().toString(),
    var contentTask: String = "",
    var dateOfTask: Long = ZonedDateTime.now(
        ZoneId.of(
            ZoneId.systemDefault().rules.getOffset(
                Instant.now()
            ).toString()
        )
    ).toEpochSecond(),
    var isNotification: Boolean = false,
    var dateOfNotification: Long = 0,
    var isDone: Boolean = false,
) : Parcelable
