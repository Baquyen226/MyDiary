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
import com.personalproject.mydiary.data.mock.MockNoteRepository
import com.personalproject.mydiary.data.mock.MockProfileRepository
import com.personalproject.mydiary.domain.folder.FolderRepository
import com.personalproject.mydiary.domain.folder.model.FolderType
import com.personalproject.mydiary.domain.profile.ProfileRepository
import com.personalproject.mydiary.domain.timeline.EntryRepository
import com.personalproject.mydiary.domain.timeline.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class FolderListViewModel(
    private val folderRepository: FolderRepository,
    private val profileRepository: ProfileRepository,
    private val entryRepository: EntryRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    val uiState: StateFlow<FolderListUiState> = combine(
        profileRepository.getProfile(),
        folderRepository.getFolders(),
        entryRepository.getEntries(),
        noteRepository.getEntries()
    ) { profile, folders, entries, notes ->
        FolderListUiState(
            profile = profile,
            folders = folders.map { folder ->
                val count = when (folder.type) {
                    FolderType.NOTES -> notes.count { it.folderId == folder.id }
                    FolderType.DIARY -> entries.count { it.folderId == folder.id }
                    FolderType.CONTACTS -> 0
                    FolderType.EVENT -> 0
                }
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
                    entryRepository = container?.entryRepository ?: MockEntryRepository(),
                    noteRepository = container?.noteRepository ?: MockNoteRepository()
                )
            }
        }
    }
}
