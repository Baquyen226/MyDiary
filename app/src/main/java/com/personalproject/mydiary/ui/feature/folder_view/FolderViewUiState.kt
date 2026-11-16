package com.personalproject.mydiary.ui.feature.folder_view

import com.personalproject.mydiary.domain.folder.model.Folder
import com.personalproject.mydiary.domain.timeline.model.DiaryEntry

data class FolderViewUiState(
    val folder: Folder? = null,
    val entries: List<DiaryEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
