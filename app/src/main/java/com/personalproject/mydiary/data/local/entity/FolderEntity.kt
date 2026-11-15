package com.personalproject.mydiary.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.personalproject.mydiary.domain.folder.model.FolderType
import java.time.Instant

@Entity(tableName = "folder")
data class FolderEntity (
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id : String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "color_hex")
    val colorHex: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Instant,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant,

    @ColumnInfo(name = "deleted_at")
    val deletedAt: Instant?,

    @ColumnInfo(name = "folder_type")
    val type : FolderType = FolderType.DIARY //make a minimal runnable version first!
)