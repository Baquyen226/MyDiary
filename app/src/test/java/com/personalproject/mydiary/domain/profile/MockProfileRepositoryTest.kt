package com.personalproject.mydiary.domain.profile

import com.personalproject.mydiary.data.local.entity.ProfileEntity
import com.personalproject.mydiary.data.local.entity.toDomain
import com.personalproject.mydiary.data.local.entity.toEntity
import com.personalproject.mydiary.data.mock.MockProfileRepository
import com.personalproject.mydiary.domain.profile.model.Profile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MockProfileRepositoryTest {

    @Test
    fun getProfile_returnsDefaultProfile() = runBlocking {
        val repository = MockProfileRepository()
        val profile = repository.getProfile().first()

        assertNotNull(profile)
        assertEquals("user_1", profile?.id)
        assertEquals("Some guy", profile?.name)
        assertEquals("+1 (555) 019-2834", profile?.phoneNumber)
        assertEquals("#4A5D74", profile?.themeColor)
    }

    @Test
    fun updateProfile_updatesAndEmitsNewProfileWithAvatarAndBannerAndThemeColor() = runBlocking {
        val repository = MockProfileRepository()

        val updated = Profile(
            id = "user_1",
            name = "Jane Doe",
            location = "Los Angeles, USA",
            note = "Mann",
            phoneNumber = "090-1234-5678",
            avatar = "content://media/avatar.jpg",
            banner = "#4A5D74",
            themeColor = "#1E81B0"
        )
        repository.updateProfile(updated)

        val profile = repository.getProfile().first()
        assertEquals("Jane Doe", profile?.name)
        assertEquals("Los Angeles, USA", profile?.location)
        assertEquals("Mann", profile?.note)
        assertEquals("090-1234-5678", profile?.phoneNumber)
        assertEquals("content://media/avatar.jpg", profile?.avatar)
        assertEquals("#4A5D74", profile?.banner)
        assertEquals("#4A5D74", profile?.background)
        assertEquals("#1E81B0", profile?.themeColor)
    }

    @Test
    fun profileEntity_toDomainAndToEntity_preservesAllFields() {
        val entity = ProfileEntity(
            id = "user_2",
            name = "Alice",
            location = "London",
            note = "Hello",
            phoneNumber = "123456",
            avatar = "avatar_path",
            banner = "banner_path",
            themeColor = "#81C784"
        )

        val domain = entity.toDomain()
        assertEquals("user_2", domain.id)
        assertEquals("Alice", domain.name)
        assertEquals("London", domain.location)
        assertEquals("Hello", domain.note)
        assertEquals("123456", domain.phoneNumber)
        assertEquals("avatar_path", domain.avatar)
        assertEquals("banner_path", domain.banner)
        assertEquals("banner_path", domain.background)
        assertEquals("#81C784", domain.themeColor)

        val entityFromDomain = domain.toEntity()
        assertEquals(entity, entityFromDomain)
    }
}
