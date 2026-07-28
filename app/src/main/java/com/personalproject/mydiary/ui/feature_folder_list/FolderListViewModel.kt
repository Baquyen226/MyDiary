package com.personalproject.mydiary.ui.feature_folder_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personalproject.mydiary.domain.model.folder.FolderRepository
import com.personalproject.mydiary.domain.model.profile.ProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class FolderListViewModel(
    private val folderRepository: FolderRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    val uiState: StateFlow<FolderListUiState> = combine(
        profileRepository.getProfile(),
        folderRepository.getFolders()
    ) { profile, folders ->
        FolderListUiState(
            profile = profile,
            folders = folders.map { FolderSummary(it, itemCount = (1..30).random()) },
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FolderListUiState(isLoading = true)
    )
}