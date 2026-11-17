package com.personalproject.mydiary.ui.feature.folder_view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.personalproject.mydiary.domain.timeline.model.DiaryEntry
import com.personalproject.mydiary.ui.feature.diary_entry.components.DiaryEntryWindow
import com.personalproject.mydiary.ui.feature.folder_view.components.CalendarTab
import com.personalproject.mydiary.ui.feature.folder_view.components.DiaryTab
import com.personalproject.mydiary.ui.feature.folder_view.components.EditorTab
import com.personalproject.mydiary.ui.feature.folder_view.components.HeaderSection
import com.personalproject.mydiary.ui.feature.folder_view.components.clearFocusOnTapOutside
import com.personalproject.mydiary.ui.theme.colorHexToCardPreference
import kotlinx.coroutines.launch

@Composable
fun FolderViewScreen(
    viewModel: FolderViewViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cardTheme = colorHexToCardPreference(uiState.folder?.color)
    var selectedEntry by remember { mutableStateOf<DiaryEntry?>(null) }

    val pagerState = rememberPagerState(initialPage = FolderTab.ENTRIES.ordinal) {
        FolderTab.entries.size
    }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // For now, this will do.
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
        coroutineScope.launch {
            pagerState.animateScrollToPage(FolderTab.ENTRIES.ordinal)
        }
    }

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
                onBackClick = onBackClick,
                tintColor = cardTheme.primaryTextColor
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = cardTheme.primaryTextColor)
            }
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) { page ->
                when (FolderTab.entries[page]) {
                    FolderTab.ENTRIES -> {
                        DiaryTab(
                            entries = uiState.entries,
                            theme = cardTheme,
                            onEntryClick = { selectedEntry = it },
                            onNewEntryClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(FolderTab.EDITOR.ordinal)
                                }
                            }
                        )
                    }
                    FolderTab.CALENDAR -> {
                        CalendarTab(
                            theme = cardTheme
                        )
                    }
                    FolderTab.EDITOR -> {
                        EditorTab(
                            theme = cardTheme,
                            onSave = { title, content ->
                                viewModel.addEntry(title, content)
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(FolderTab.ENTRIES.ordinal)
                                }
                            },
                            onDiscard = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(FolderTab.ENTRIES.ordinal)
                                }
                            }
                        )
                    }
                }
            }
        }

        selectedEntry?.let { entry ->
            DiaryEntryWindow(
                entry = entry,
                theme = cardTheme,
                onDismiss = { selectedEntry = null }
            )
        }
    }
}
