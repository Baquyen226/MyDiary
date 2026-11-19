package com.personalproject.mydiary.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Database representation of a diary entry in Room.
 * Supports rich text (HTML, not touching this ever
 * again), mood tracking, weather conditions,
 * Foreign-key link to [FolderEntity].
 */
@Entity(
    tableName = "entries",
    foreignKeys = [
        ForeignKey(
            entity = FolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["folder_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["folder_id", "entry_date"]),
        Index(value = ["entry_date"]),
        Index(value = ["entry_timestamp"])
    ]
)
data class EntryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "folder_id")
    val folderId: String? = null,

    @ColumnInfo(name = "title")
    val title: String = "",

    @ColumnInfo(name = "body")
    val body: String = "",

    @ColumnInfo(name = "body_plain", defaultValue = "")
    val bodyPlain: String = "",

    @ColumnInfo(name = "body_format", defaultValue = "HTML")
    val bodyFormat: String = "HTML",

    @ColumnInfo(name = "color_hex")
    val colorHex: String? = null,

    @ColumnInfo(name = "mood_score", defaultValue = "0")
    val moodScore: Int = 0,

    @ColumnInfo(name = "weather_summary", defaultValue = "UNKNOWN")
    val weatherSummary: String = "UNKNOWN",

    @ColumnInfo(name = "temperature_high", defaultValue = "0")
    val temperatureHigh: Int = 0,

    @ColumnInfo(name = "temperature_low", defaultValue = "0")
    val temperatureLow: Int = 0,

    @ColumnInfo(name = "location_name")
    val locationName: String? = null,

    @ColumnInfo(name = "latitude")
    val latitude: Double? = null,

    @ColumnInfo(name = "longitude")
    val longitude: Double? = null,

    @ColumnInfo(name = "entry_timestamp")
    val entryTimestamp: Instant = Instant.now(),

    @ColumnInfo(name = "entry_date")
    val entryDate: LocalDate = entryTimestamp.atZone(ZoneId.systemDefault()).toLocalDate(),

    @ColumnInfo(name = "is_favorite", defaultValue = "0")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "is_bookmarked", defaultValue = "0")
    val isBookmarked: Boolean = false,

    @ColumnInfo(name = "voice_note_uri")
    val voiceNoteUri: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.now(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant = Instant.now(),

    @ColumnInfo(name = "deleted_at")
    val deletedAt: Instant? = null
)
