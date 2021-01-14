package kz.stepanenkos.notes.common.roomdatabase

import androidx.room.TypeConverter
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime


class NotesTypeConverters {
    @TypeConverter
    fun fromZonedDateTime(date: ZonedDateTime): Long {
        return date.toEpochSecond()
    }

    @TypeConverter
    fun toZonedDateTime(epochSecond: Long): ZonedDateTime {
        val ldt = LocalDateTime.ofEpochSecond(
            epochSecond,
            0,
            ZoneOffset.systemDefault().rules.getOffset(Instant.now()))
        return ZonedDateTime.of(ldt, ZoneOffset.systemDefault())
    }
}