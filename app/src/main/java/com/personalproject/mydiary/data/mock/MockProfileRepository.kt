package com.personalproject.mydiary.data.mock

import com.personalproject.mydiary.domain.profile.ProfileRepository
import com.personalproject.mydiary.domain.profile.model.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockProfileRepository : ProfileRepository {
    override fun getProfile(): Flow<Profile?> {
        return flowOf(
            Profile(
                id = "user_1",
                name = "Some guy",
                location = "I swallowed a whole pack of gum yesterday",
                note = "I love diaries!"
            )
        )
    }
}