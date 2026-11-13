package com.personalproject.mydiary.domain.profile

import com.personalproject.mydiary.domain.profile.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfile(): Flow<Profile?>
}