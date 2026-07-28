package com.personalproject.mydiary.data.profile.cache

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
class ProfileEntity (
    @PrimaryKey
    val uuid : String?,
    val name : String?,
    val location : String?,
    val note : String?
)