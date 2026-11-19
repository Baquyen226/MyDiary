package com.personalproject.mydiary.ui.feature.folder_view

import androidx.annotation.StringRes
import com.personalproject.mydiary.R

enum class FolderTab(@StringRes val titleRes: Int) {
    ENTRIES(R.string.ui_folder_view_entries),
    CALENDAR(R.string.ui_folder_view_calendar),
    EDITOR(R.string.ui_folder_view_diary)
}
