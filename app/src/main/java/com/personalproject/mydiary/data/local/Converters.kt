package com.personalproject.mydiary.data.local

import androidx.room.TypeConverter
import com.personalproject.mydiary.domain.folder.model.FolderType
import java.time.Instant
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun instantToTimestamp(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }

    @TypeConverter
    fun fromLocalDateString(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun localDateToString(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromFolderTypeString(value: String?): FolderType? {
        return value?.let { FolderType.valueOf(it) }
    }

    @TypeConverter
    fun folderTypeToString(type: FolderType?): String? {
        return type?.name
    }
}
