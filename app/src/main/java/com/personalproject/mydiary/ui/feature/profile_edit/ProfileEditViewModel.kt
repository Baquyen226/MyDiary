package com.personalproject.mydiary.ui.feature.profile_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.personalproject.mydiary.MyDiaryApplication
import com.personalproject.mydiary.data.mock.MockProfileRepository
import com.personalproject.mydiary.domain.profile.ProfileRepository
import com.personalproject.mydiary.domain.profile.model.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileEditUiState(
    val id: String = "",
    val name: String = "",
    val location: String = "",
    val phoneNumber: String = "",
    val note: String = "",
    val avatar: String = "",
    val banner: String = "",
    val themeColor: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false
) {
    val background: String get() = banner
}

class ProfileEditViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileEditUiState())
    val uiState: StateFlow<ProfileEditUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val profile = profileRepository.getProfile().firstOrNull()
            _uiState.update {
                it.copy(
                    id = profile?.id ?: "user_1",
                    name = profile?.name.orEmpty(),
                    location = profile?.location.orEmpty(),
                    phoneNumber = profile?.phoneNumber.orEmpty(),
                    note = profile?.note.orEmpty(),
                    avatar = profile?.avatar.orEmpty(),
                    banner = profile?.banner ?: profile?.background.orEmpty(),
                    themeColor = profile?.themeColor.orEmpty(),
                    isLoading = false
                )
            }
        }
    }

    fun onThemeColorChange(themeColor: String) {
        _uiState.update { it.copy(themeColor = themeColor) }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onLocationChange(location: String) {
        _uiState.update { it.copy(location = location) }
    }

    fun onPhoneNumberChange(phoneNumber: String) {
        _uiState.update { it.copy(phoneNumber = phoneNumber) }
    }

    fun onNoteChange(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun onAvatarChange(avatar: String) {
        _uiState.update { it.copy(avatar = avatar) }
    }

    fun onBannerChange(banner: String) {
        _uiState.update { it.copy(banner = banner) }
    }

    fun onBackgroundChange(background: String) {
        onBannerChange(background)
    }

    fun saveProfile(onSuccess: () -> Unit) {
        val current = _uiState.value
        if (current.isSaving) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val updated = Profile(
                id = current.id.ifEmpty { "user_1" },
                name = current.name.trim(),
                location = current.location.trim(),
                note = current.note.trim(),
                phoneNumber = current.phoneNumber.trim(),
                avatar = current.avatar.trim().ifEmpty { null },
                banner = current.banner.trim().ifEmpty { null },
                themeColor = current.themeColor.trim().ifEmpty { null }
            )
            profileRepository.updateProfile(updated)
            _uiState.update { it.copy(isSaving = false) }
            onSuccess()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as? MyDiaryApplication
                val container = app?.container
                ProfileEditViewModel(
                    profileRepository = container?.profileRepository ?: MockProfileRepository()
                )
            }
        }
    }
}
