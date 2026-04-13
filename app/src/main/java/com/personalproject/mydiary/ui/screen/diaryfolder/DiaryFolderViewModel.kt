package com.personalproject.mydiary.ui.screen.diaryfolder

import androidx.lifecycle.ViewModel
import com.personalproject.mydiary.data.mock.MockDiary
import com.personalproject.mydiary.data.model.folder.DiaryFolder
import com.personalproject.mydiary.data.model.timelineItem.DiaryEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DiaryFolderViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DiaryFolderUiState())
    val uiState: StateFlow<DiaryFolderUiState> = _uiState.asStateFlow()

    fun openFolder(diaryEntry: DiaryEntry) {
        _uiState.update { it.copy(isWindowOpen = true) }
    }
}

data class DiaryFolderUiState(
    val isWindowOpen: Boolean = false,
    val diaryFolder: DiaryFolder = DiaryFolder("test", MockDiary.mockFolder),
    val selectedEntry: DiaryEntry? = null
)