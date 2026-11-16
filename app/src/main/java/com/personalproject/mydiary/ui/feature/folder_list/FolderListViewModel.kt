package com.personalproject.mydiary.ui.feature.folder_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.personalproject.mydiary.MyDiaryApplication
import com.personalproject.mydiary.data.mock.MockEntryRepository
import com.personalproject.mydiary.data.mock.MockFolderRepository
import com.personalproject.mydiary.data.mock.MockProfileRepository
import com.personalproject.mydiary.domain.folder.FolderRepository
import com.personalproject.mydiary.domain.profile.ProfileRepository
import com.personalproject.mydiary.domain.timeline.EntryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class FolderListViewModel(
    private val folderRepository: FolderRepository,
    private val profileRepository: ProfileRepository,
    private val entryRepository: EntryRepository
) : ViewModel() {

    val uiState: StateFlow<FolderListUiState> = combine(
        profileRepository.getProfile(),
        folderRepository.getFolders(),
        entryRepository.getEntries()
    ) { profile, folders, entries ->
        FolderListUiState(
            profile = profile,
            folders = folders.map { folder ->
                val count = entries.count { it.folderId == folder.id }
                FolderSummary(folder = folder, itemCount = count)
            },
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FolderListUiState(isLoading = true)
    )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as? MyDiaryApplication
                val container = app?.container
                FolderListViewModel(
                    folderRepository = container?.folderRepository ?: MockFolderRepository(),
                    profileRepository = container?.profileRepository ?: MockProfileRepository(),
                    entryRepository = container?.entryRepository ?: MockEntryRepository()
                )
            }
        }
    }
}
