package com.personalproject.mydiary.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

import java.time.Instant
import java.time.LocalDate

/**
 * Try yer best to support rtf!
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
        Index(value = ["folder_id", "entry_date"]), //To sort by date later
        Index(value = ["entry_date"])
    ]
)
data class EntryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "folder_id")
    val folderUuid: String? = null,

    @ColumnInfo(name = "title")
    val title: String? = null,

    @ColumnInfo(name = "body")
    val body: String,

    @ColumnInfo(name = "color_hex")
    val colorHex: String,

    //Depending on what I'll do this can support multiple formats like HTML n
    //stuff. Might be a pain in the ass tho
    @ColumnInfo(name = "body_format", defaultValue = "HTML")
    val bodyFormat: String = "HTML",

    @ColumnInfo(name = "body_plain", defaultValue = "")
    val bodyPlain: String = "",

    @ColumnInfo(name = "location_name")
    val locationName: String?,

    @ColumnInfo(name = "longitude")
    val longitude: Double,

    @ColumnInfo(name = "latitude")
    val latitude: Double,

    @ColumnInfo(name = "entry_date")
    val entryDate: LocalDate,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean,

    @ColumnInfo(name = "is_bookmarked")
    val isBookmarked: Boolean,

    @ColumnInfo(name = "temperature_high")
    val temperatureHigh: Int,

    @ColumnInfo(name = "temperature_low")
    val temperatureLow: Int,

    @ColumnInfo(name = "created_at")
    val createdAt: Instant,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant,

    @ColumnInfo(name = "deleted_at")
    val deletedAt: Instant? = null
)
