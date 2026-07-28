package com.personalproject.mydiary.domain.model.profile

import com.personalproject.mydiary.domain.model.profile.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfile(): Flow<Profile?>
}