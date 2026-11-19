package com.personalproject.mydiary.ui.feature.folder_view

import com.personalproject.mydiary.domain.folder.model.Folder
import com.personalproject.mydiary.domain.timeline.model.DiaryEntry
import com.personalproject.mydiary.domain.timeline.model.NoteEntry

data class FolderViewUiState(
    val folder: Folder? = null,
    val entries: List<DiaryEntry> = emptyList(),
    val notes: List<NoteEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
