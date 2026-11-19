package com.personalproject.mydiary.ui.feature.folder_view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.personalproject.mydiary.core.util.toPlainText
import com.personalproject.mydiary.domain.timeline.model.DiaryEntry
import com.personalproject.mydiary.ui.feature.common_component.editor.rememberDiaryEditorState
import com.personalproject.mydiary.ui.feature.diary_entry.components.diary_window.DiaryEntryWindow
import com.personalproject.mydiary.ui.feature.diary_entry.components.diary_window.MoodPickerDialog
import com.personalproject.mydiary.ui.feature.folder_view.components.CalendarTab
import com.personalproject.mydiary.ui.feature.folder_view.components.DiaryTab
import com.personalproject.mydiary.ui.feature.folder_view.components.EditorTab
import com.personalproject.mydiary.ui.feature.folder_view.components.FolderViewBottomBar
import com.personalproject.mydiary.ui.feature.folder_view.components.HeaderSection
import com.personalproject.mydiary.ui.feature.folder_view.components.clearFocusOnTapOutside
import com.personalproject.mydiary.ui.theme.colorHexToCardPreference
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FolderViewScreen(
    viewModel: FolderViewViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cardTheme = colorHexToCardPreference(uiState.folder?.color)
    var selectedEntry by remember { mutableStateOf<DiaryEntry?>(null) }
    var editingEntry by remember { mutableStateOf<DiaryEntry?>(null) }
    var moodDialogEntry by remember { mutableStateOf<DiaryEntry?>(null) }

    val pagerState = rememberPagerState(initialPage = FolderTab.ENTRIES.ordinal) {
        FolderTab.entries.size
    }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val editorState = rememberDiaryEditorState(
        onSave = { title, contentHtml, imageUris ->
            val currentEditing = editingEntry
            if (currentEditing != null) {
                viewModel.updateEntry(
                    currentEditing.copy(
                        title = title,
                        body = contentHtml,
                        bodyPlain = contentHtml.toPlainText(),
                        imageUris = imageUris
                    )
                )
                editingEntry = null
            } else {
                viewModel.addEntry(
                    title = title,
                    content = contentHtml,
                    imageUris = imageUris
                )
            }
            coroutineScope.launch {
                pagerState.animateScrollToPage(FolderTab.ENTRIES.ordinal)
            }
        },
        onDiscard = {
            editingEntry = null
            coroutineScope.launch {
                pagerState.animateScrollToPage(FolderTab.ENTRIES.ordinal)
            }
        }
    )

    // Unfocus and dismiss keyboard when navigating to other tabs
    LaunchedEffect(pagerState.targetPage) {
        if (pagerState.targetPage != FolderTab.EDITOR.ordinal) {
            focusManager.clearFocus(force = true)
            keyboardController?.hide()
        }
    }

    // Also dismiss keyboard immediately when the user starts swiping/scrolling between tabs
    LaunchedEffect(pagerState.isScrollInProgress) {
        if (pagerState.isScrollInProgress) {
            focusManager.clearFocus(force = true)
            keyboardController?.hide()
        }
    }

    // When Back is pressed, if user is not on the Entries tab, return to Entries first
    BackHandler(enabled = pagerState.currentPage != FolderTab.ENTRIES.ordinal) {
        editingEntry = null
        coroutineScope.launch {
            pagerState.animateScrollToPage(FolderTab.ENTRIES.ordinal)
        }
    }

    val density = LocalDensity.current
    val isDockedImePresent = WindowInsets.ime.getBottom(density) > 0
    val isEditorTab = FolderTab.entries[pagerState.currentPage] == FolderTab.EDITOR
    val shouldHideBottomBar = isEditorTab && isDockedImePresent

    Scaffold(
        modifier = modifier.clearFocusOnTapOutside(),
        topBar = {
            HeaderSection(
                title = uiState.folder?.name ?: "Diary",
                selectedTab = FolderTab.entries[pagerState.currentPage],
                onTabSelect = { tab ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(tab.ordinal)
                    }
                },
                onBackClick = {
                    if (pagerState.currentPage != FolderTab.ENTRIES.ordinal) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(FolderTab.ENTRIES.ordinal)
                        }
                    } else {
                        onBackClick()
                    }
                },
                tintColor = cardTheme.primaryTextColor
            )
        },
        bottomBar = {
            // Automatically hides the bottom bar only in the editor tab when an on-screen keyboard is docked in place
            if (!shouldHideBottomBar) {
                FolderViewBottomBar(
                    currentTab = FolderTab.entries[pagerState.currentPage],
                    theme = cardTheme,
                    entriesCount = uiState.entries.size,
                    onNewEntryClick = {
                        editingEntry = null
                        editorState.reset()
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(FolderTab.EDITOR.ordinal)
                        }
                    },
                    onDiscardEditorClick = {
                        editorState.discard()
                    },
                    onSaveEditorClick = {
                        editorState.save()
                    }
                )
            }
        }
    ) { innerPadding ->
        // When the docked onscreen keyboard is open in editor, remove bottom padding so the editor rests directly against the keyboard
        val contentBottomPadding = if (shouldHideBottomBar) 0.dp else innerPadding.calculateBottomPadding()

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        bottom = contentBottomPadding
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = cardTheme.primaryTextColor)
            }
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        bottom = contentBottomPadding
                    )
            ) { page ->
                when (FolderTab.entries[page]) {
                    FolderTab.ENTRIES -> {
                        DiaryTab(
                            entries = uiState.entries,
                            theme = cardTheme,
                            onEntryClick = { selectedEntry = it },
                            onMoodClick = { moodDialogEntry = it }
                        )
                    }
                    FolderTab.CALENDAR -> {
                        CalendarTab(
                            theme = cardTheme
                        )
                    }
                    FolderTab.EDITOR -> {
                        val editorTheme = editingEntry?.colorHex?.takeIf { it.isNotBlank() }?.let { colorHexToCardPreference(it) } ?: cardTheme
                        EditorTab(
                            theme = editorTheme,
                            editorState = editorState
                        )
                    }
                }
            }
        }

        selectedEntry?.let { entry ->
            val latestEntry = uiState.entries.find { it.id == entry.id } ?: entry
            val entryTheme = colorHexToCardPreference(latestEntry.colorHex ?: uiState.folder?.color)
            DiaryEntryWindow(
                entry = latestEntry,
                theme = entryTheme,
                onDismiss = { selectedEntry = null },
                onEditClick = {
                    editingEntry = latestEntry
                    editorState.loadEntry(latestEntry)
                    selectedEntry = null
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(FolderTab.EDITOR.ordinal)
                    }
                },
                onColorChange = { newColorHex ->
                    val updated = latestEntry.copy(colorHex = newColorHex)
                    viewModel.updateEntry(updated)
                    selectedEntry = updated
                },
                onMoodChange = { newMoodScore ->
                    val updated = latestEntry.copy(moodScore = newMoodScore)
                    viewModel.updateEntry(updated)
                    selectedEntry = updated
                }
            )
        }

        moodDialogEntry?.let { entry ->
            val entryTheme = colorHexToCardPreference(entry.colorHex ?: uiState.folder?.color)
            MoodPickerDialog(
                currentMood = entry.moodScore,
                theme = entryTheme,
                onDismiss = { moodDialogEntry = null },
                onMoodSelected = { newMood ->
                    val updated = entry.copy(moodScore = newMood)
                    viewModel.updateEntry(updated)
                    if (selectedEntry?.id == entry.id) {
                        selectedEntry = updated
                    }
                    moodDialogEntry = null
                }
            )
        }
    }
}
