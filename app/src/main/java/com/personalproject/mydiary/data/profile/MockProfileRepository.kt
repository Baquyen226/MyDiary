package com.personalproject.mydiary.data.profile

import com.personalproject.mydiary.domain.model.profile.ProfileRepository
import com.personalproject.mydiary.domain.model.profile.model.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockProfileRepository : ProfileRepository {
    override fun getProfile(): Flow<Profile?> {
        return flowOf(
            Profile(
                uuid = "user_1",
                name = "Some guy",
                location = "I swallowed a whole pack of gum yesterday",
                note = "I love diaries!"
            )
        )
    }
}
