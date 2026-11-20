package com.personalproject.mydiary.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.personalproject.mydiary.domain.profile.model.Profile

@Entity(tableName = "profile")
data class ProfileEntity (
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String?,

    @ColumnInfo(name = "location")
    val location: String?,

    @ColumnInfo(name = "note")
    val note: String?,

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String? = null,

    @ColumnInfo(name = "avatar")
    val avatar: String? = null,

    @ColumnInfo(name = "banner")
    val banner: String? = null,

    @ColumnInfo(name = "theme_color")
    val themeColor: String? = null
) {
    /**
     * Backward-compatible alias for [avatar].
     */
    val profilePicUri: String? get() = avatar

    /**
     * Backward-compatible alias for [banner].
     */
    val background: String? get() = banner
}

fun ProfileEntity.toDomain(): Profile = Profile(
    id = id,
    name = name,
    location = location,
    note = note,
    phoneNumber = phoneNumber,
    avatar = avatar,
    banner = banner,
    themeColor = themeColor
)

fun Profile.toEntity(): ProfileEntity = ProfileEntity(
    id = id ?: "user_1",
    name = name,
    location = location,
    note = note,
    phoneNumber = phoneNumber,
    avatar = avatar,
    banner = banner,
    themeColor = themeColor
)
