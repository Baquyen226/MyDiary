package com.personalproject.mydiary.ui.feature.folder_list

import com.personalproject.mydiary.domain.folder.model.Folder
import com.personalproject.mydiary.domain.profile.model.Profile

data class FolderListUiState(
    val profile: Profile? = null,
    val folders: List<FolderSummary> = emptyList(),
    val tags: List<String> = listOf("ニュース", "学校体育", "バイト"),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class FolderSummary(
    val folder: Folder,
    val itemCount: Int
)
