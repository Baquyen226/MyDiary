package com.personalproject.mydiary.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class ProfileEntity (
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name : String?,

    @ColumnInfo(name = "location")
    val location : String?,

    @ColumnInfo(name = "note")
    val note : String?,

    @ColumnInfo(name = "profile_pic")
    val profilePicUri : String
)