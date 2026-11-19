package com.personalproject.mydiary.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.UUID

/**
 * Represents a media attachment (photo, camera capture, voice note, or file)
 * associated with a [EntryEntity].
 *
 * Configured with CASCADE deletion so attachments are automatically cleaned up
 * when the parent entry is deleted from the database.
 */
@Entity(
    tableName = "media_attachments",
    foreignKeys = [
        ForeignKey(
            entity = EntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["entry_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["entry_id"]),
        Index(value = ["entry_id", "display_order"])
    ]
)
data class MediaAttachmentEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "entry_id")
    val entryId: String,

    @ColumnInfo(name = "uri")
    val uri: String,

    @ColumnInfo(name = "media_type", defaultValue = "IMAGE")
    val mediaType: String = "IMAGE", // "IMAGE", "VOICE", "AUDIO", "FILE"

    @ColumnInfo(name = "file_name")
    val fileName: String? = null,

    @ColumnInfo(name = "file_size_bytes")
    val fileSizeBytes: Long? = null,

    @ColumnInfo(name = "duration_ms")
    val durationMs: Long? = null,

    @ColumnInfo(name = "display_order", defaultValue = "0")
    val displayOrder: Int = 0,

    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.now()
)
