package com.personalproject.mydiary.ui.feature_folder_list

import com.personalproject.mydiary.domain.model.folder.model.Folder
import com.personalproject.mydiary.domain.model.profile.model.Profile

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