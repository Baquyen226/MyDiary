package com.personalproject.mydiary.data.local.mapper

import com.personalproject.mydiary.data.local.entity.ProfileEntity
import com.personalproject.mydiary.domain.profile.model.Profile

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
