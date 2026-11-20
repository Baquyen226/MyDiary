package com.personalproject.mydiary.data.mock

import com.personalproject.mydiary.domain.profile.ProfileRepository
import com.personalproject.mydiary.domain.profile.model.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockProfileRepository : ProfileRepository {
    private val profileState = MutableStateFlow<Profile?>(
        Profile(
            id = "user_1",
            name = "Some guy",
            location = "I swallowed a whole pack of gum yesterday",
            note = "I love diaries!",
            phoneNumber = "+1 (555) 019-2834",
            themeColor = "#4A5D74"
        )
    )

    override fun getProfile(): Flow<Profile?> {
        return profileState.asStateFlow()
    }

    override suspend fun updateProfile(profile: Profile) {
        profileState.value = profile
    }
}