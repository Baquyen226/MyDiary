package com.personalproject.mydiary.ui.feature.folder_view

import androidx.annotation.StringRes
import com.personalproject.mydiary.R
import com.personalproject.mydiary.domain.folder.model.FolderType

enum class FolderTab {
    ENTRIES,
    CALENDAR,
    EDITOR;

    @StringRes
    fun titleRes(folderType: FolderType = FolderType.DIARY): Int = when (this) {
        ENTRIES -> R.string.ui_folder_view_entries
        CALENDAR -> R.string.ui_folder_view_calendar
        EDITOR -> when (folderType) {
            FolderType.NOTES -> R.string.ui_folder_view_editor
            else -> R.string.ui_folder_view_diary
        }
    }

    @Deprecated("Use titleRes(folderType) instead", ReplaceWith("titleRes()"))
    val titleRes: Int
        @StringRes get() = titleRes()
}
