package com.personalproject.mydiary.ui.feature.folder_view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.personalproject.mydiary.MyDiaryApplication
import com.personalproject.mydiary.data.mock.MockEntryRepository
import com.personalproject.mydiary.data.mock.MockFolderRepository
import com.personalproject.mydiary.domain.folder.FolderRepository
import com.personalproject.mydiary.domain.timeline.EntryRepository
import com.personalproject.mydiary.domain.timeline.model.DiaryEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

class FolderViewViewModel(
    private val folderId: String,
    private val folderRepository: FolderRepository,
    private val entryRepository: EntryRepository
) : ViewModel() {

    val uiState: StateFlow<FolderViewUiState> = combine(
        folderRepository.getFolderById(folderId),
        entryRepository.observeEntriesFromFolder(folderId)
    ) { folder, entries ->
        FolderViewUiState(
            folder = folder,
            entries = entries,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FolderViewUiState(isLoading = true)
    )

    fun addEntry(
        title: String,
        content: String,
        imageUris: List<String> = emptyList(),
        voiceNoteUri: String? = null
    ) {
        val entry = DiaryEntry(
            id = UUID.randomUUID().toString(),
            folderId = folderId,
            timestamp = Instant.now(),
            title = title,
            content = content,
            imageUris = imageUris,
            voiceNoteUri = voiceNoteUri
        )
        viewModelScope.launch {
            entryRepository.addEntry(entry)
        }
    }

    fun updateEntry(entry: DiaryEntry) {
        viewModelScope.launch {
            entryRepository.update(entry)
        }
    }

    companion object {
        fun provideFactory(folderId: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as? MyDiaryApplication
                val container = app?.container
                FolderViewViewModel(
                    folderId = folderId,
                    folderRepository = container?.folderRepository ?: MockFolderRepository(),
                    entryRepository = container?.entryRepository ?: MockEntryRepository()
                )
            }
        }
    }
}
