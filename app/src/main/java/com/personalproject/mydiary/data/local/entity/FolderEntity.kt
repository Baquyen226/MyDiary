package com.personalproject.mydiary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.personalproject.mydiary.domain.folder.model.FolderType

@Entity(tableName = "folder")
data class FolderEntity (
    @PrimaryKey
    val uuid : String,
    val name: String,
    val type : FolderType
)