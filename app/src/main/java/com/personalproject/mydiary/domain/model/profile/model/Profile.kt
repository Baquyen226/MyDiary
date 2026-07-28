package com.personalproject.mydiary.domain.model.profile.model

// Only one user exists per Android profile.
class Profile (
    val uuid : String?,
    val name : String?,
    val location : String?,
    val note : String?
)