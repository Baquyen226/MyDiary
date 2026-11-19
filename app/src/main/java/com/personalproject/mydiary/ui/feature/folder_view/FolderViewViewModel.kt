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
import com.personalproject.mydiary.data.mock.MockNoteRepository
import com.personalproject.mydiary.domain.folder.FolderRepository
import com.personalproject.mydiary.domain.timeline.EntryRepository
import com.personalproject.mydiary.domain.timeline.NoteRepository
import com.personalproject.mydiary.domain.timeline.model.DiaryEntry
import com.personalproject.mydiary.domain.timeline.model.NoteEntry
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
    private val entryRepository: EntryRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    val uiState: StateFlow<FolderViewUiState> = combine(
        folderRepository.getFolderById(folderId),
        entryRepository.observeEntriesFromFolder(folderId),
        noteRepository.observeEntriesFromFolder(folderId)
    ) { folder, entries, notes ->
        FolderViewUiState(
            folder = folder,
            entries = entries,
            notes = notes,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FolderViewUiState(isLoading = true)
    )

    // Diary Entry operations
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
            colorHex = uiState.value.folder?.color,
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

    fun deleteEntry(entryId: String) {
        viewModelScope.launch {
            entryRepository.deleteEntry(entryId)
        }
    }

    // Note operations
    fun addNote(
        title: String,
        description: String? = null,
        content: String,
        imageUris: List<String> = emptyList()
    ) {
        val note = NoteEntry(
            id = UUID.randomUUID().toString(),
            folderId = folderId,
            timestamp = Instant.now(),
            title = title,
            description = description,
            content = content,
            colorHex = uiState.value.folder?.color,
            imageUris = imageUris
        )
        viewModelScope.launch {
            noteRepository.addEntry(note)
        }
    }

    fun updateNote(note: NoteEntry) {
        viewModelScope.launch {
            noteRepository.update(note)
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            noteRepository.deleteEntry(noteId)
        }
    }

    fun toggleBookmarkNote(note: NoteEntry) {
        viewModelScope.launch {
            noteRepository.update(note.copy(isBookmarked = !note.isBookmarked))
        }
    }

    fun togglePinNote(note: NoteEntry) {
        viewModelScope.launch {
            noteRepository.update(note.copy(isPinned = !note.isPinned))
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
                    entryRepository = container?.entryRepository ?: MockEntryRepository(),
                    noteRepository = container?.noteRepository ?: MockNoteRepository()
                )
            }
        }
    }
}
