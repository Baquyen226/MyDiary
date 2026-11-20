package com.personalproject.mydiary.ui.feature.folder_view

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.personalproject.mydiary.core.util.LocationUtil
import com.personalproject.mydiary.core.util.toPlainText
import com.personalproject.mydiary.domain.folder.model.FolderType
import com.personalproject.mydiary.domain.timeline.model.DiaryEntry
import com.personalproject.mydiary.domain.timeline.model.NoteEntry
import com.personalproject.mydiary.ui.feature.common_component.editor.rememberDiaryEditorState
import com.personalproject.mydiary.ui.feature.entry.diary_entry.components.diary_window.DiaryEntryWindow
import com.personalproject.mydiary.ui.feature.entry.diary_entry.components.diary_window.MoodPickerDialog
import com.personalproject.mydiary.ui.feature.entry.note_entry.components.note_window.NoteWindow
import com.personalproject.mydiary.ui.feature.folder_view.components.AlarmsPlaceholderTab
import com.personalproject.mydiary.ui.feature.folder_view.components.CalendarTab
import com.personalproject.mydiary.ui.feature.folder_view.components.ContactsPlaceholderTab
import com.personalproject.mydiary.ui.feature.folder_view.components.DiaryTab
import com.personalproject.mydiary.ui.feature.folder_view.components.EditorTab
import com.personalproject.mydiary.ui.feature.folder_view.components.FolderViewBottomBar
import com.personalproject.mydiary.ui.feature.folder_view.components.HeaderSection
import com.personalproject.mydiary.ui.feature.folder_view.components.NoteTab
import com.personalproject.mydiary.ui.feature.folder_view.components.clearFocusOnTapOutside
import com.personalproject.mydiary.ui.theme.rememberTimelineCardPreference
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FolderViewScreen(
    viewModel: FolderViewViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cardTheme = rememberTimelineCardPreference(uiState.folder?.color)
    val folderType = uiState.folder?.type ?: FolderType.DIARY

    var selectedEntry by remember { mutableStateOf<DiaryEntry?>(null) }
    var editingEntry by remember { mutableStateOf<DiaryEntry?>(null) }
    var moodDialogEntry by remember { mutableStateOf<DiaryEntry?>(null) }

    var selectedNote by remember { mutableStateOf<NoteEntry?>(null) }
    var editingNote by remember { mutableStateOf<NoteEntry?>(null) }

    val pagerState = rememberPagerState(initialPage = FolderTab.ENTRIES.ordinal) {
        FolderTab.entries.size
    }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    var pendingSavePayload by remember {
        mutableStateOf<Triple<String, String, List<String>>?>(null)
    }
    var pendingLocationRefreshForEntry by remember {
        mutableStateOf<DiaryEntry?>(null)
    }

    val commitSave: (String, String, List<String>, String) -> Unit = { title, contentHtml, imageUris, location ->
        if (folderType == FolderType.NOTES) {
            val currentEditing = editingNote
            if (currentEditing != null) {
                viewModel.updateNote(
                    currentEditing.copy(
                        title = title,
                        body = contentHtml,
                        bodyPlain = contentHtml.toPlainText(),
                        imageUris = imageUris,
                        location = if (currentEditing.location.isNullOrBlank() || currentEditing.location == LocationUtil.UNKNOWN_LOCATION) {
                            location
                        } else {
                            currentEditing.location
                        }
                    )
                )
                editingNote = null
            } else {
                viewModel.addNote(
                    title = title,
                    content = contentHtml,
                    imageUris = imageUris,
                    location = location
                )
            }
        } else {
            val currentEditing = editingEntry
            if (currentEditing != null) {
                viewModel.updateEntry(
                    currentEditing.copy(
                        title = title,
                        body = contentHtml,
                        bodyPlain = contentHtml.toPlainText(),
                        imageUris = imageUris,
                        location = if (currentEditing.location.isNullOrBlank() || currentEditing.location == LocationUtil.UNKNOWN_LOCATION) {
                            location
                        } else {
                            currentEditing.location
                        }
                    )
                )
                editingEntry = null
            } else {
                viewModel.addEntry(
                    title = title,
                    content = contentHtml,
                    imageUris = imageUris,
                    location = location
                )
            }
        }
        coroutineScope.launch {
            pagerState.animateScrollToPage(FolderTab.ENTRIES.ordinal)
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = LocationUtil.isAnyPermissionGranted(permissions)

        val savePayload = pendingSavePayload
        pendingSavePayload = null
        if (savePayload != null) {
            val (title, contentHtml, imageUris) = savePayload
            coroutineScope.launch {
                val location = if (isGranted) {
                    LocationUtil.resolveLocation(context)
                } else {
                    LocationUtil.UNKNOWN_LOCATION
                }
                commitSave(title, contentHtml, imageUris, location)
            }
        }

        val refreshEntry = pendingLocationRefreshForEntry
        pendingLocationRefreshForEntry = null
        if (refreshEntry != null) {
            coroutineScope.launch {
                val location = if (isGranted) {
                    LocationUtil.resolveLocation(context)
                } else {
                    LocationUtil.UNKNOWN_LOCATION
                }
                val updated = refreshEntry.copy(location = location)
                viewModel.updateEntry(updated)
                if (selectedEntry?.id == updated.id) {
                    selectedEntry = updated
                }
            }
        }
    }

    val editorState = rememberDiaryEditorState(
        onSave = { title, contentHtml, imageUris ->
            if (LocationUtil.hasLocationPermission(context)) {
                coroutineScope.launch {
                    val location = LocationUtil.resolveLocation(context)
                    commitSave(title, contentHtml, imageUris, location)
                }
            } else {
                pendingSavePayload = Triple(title, contentHtml, imageUris)
                locationPermissionLauncher.launch(LocationUtil.LOCATION_PERMISSIONS)
            }
        },
        onDiscard = {
            editingEntry = null
            editingNote = null
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

    // Back handling
    val isPlaceholderFolder = folderType == FolderType.CONTACTS || folderType == FolderType.EVENT
    BackHandler(enabled = !isPlaceholderFolder && pagerState.currentPage != FolderTab.ENTRIES.ordinal) {
        editingEntry = null
        editingNote = null
        coroutineScope.launch {
            pagerState.animateScrollToPage(FolderTab.ENTRIES.ordinal)
        }
    }

    val density = LocalDensity.current
    val isDockedImePresent = WindowInsets.ime.getBottom(density) > 0
    val isEditorTab = FolderTab.entries[pagerState.currentPage] == FolderTab.EDITOR
    val shouldHideBottomBar = isEditorTab && isDockedImePresent
    val totalCount = if (folderType == FolderType.NOTES) uiState.notes.size else uiState.entries.size

    Scaffold(
        modifier = modifier.clearFocusOnTapOutside(),
        topBar = {
            HeaderSection(
                title = uiState.folder?.name ?: "Folder",
                folderType = folderType,
                selectedTab = FolderTab.entries[pagerState.currentPage],
                onTabSelect = { tab ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(tab.ordinal)
                    }
                },
                onBackClick = {
                    if (!isPlaceholderFolder && pagerState.currentPage != FolderTab.ENTRIES.ordinal) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(FolderTab.ENTRIES.ordinal)
                        }
                    } else {
                        onBackClick()
                    }
                },
                tintColor = cardTheme.primaryTextColor,
                showTabs = !isPlaceholderFolder
            )
        },
        bottomBar = {
            if (!isPlaceholderFolder && !shouldHideBottomBar) {
                FolderViewBottomBar(
                    currentTab = FolderTab.entries[pagerState.currentPage],
                    theme = cardTheme,
                    entriesCount = totalCount,
                    onNewEntryClick = {
                        editingEntry = null
                        editingNote = null
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
        } else if (folderType == FolderType.CONTACTS) {
            // Contacts Folder View (Placeholder)
            ContactsPlaceholderTab(
                theme = cardTheme,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        bottom = contentBottomPadding
                    )
            )
        } else if (folderType == FolderType.EVENT) {
            // Alarms & Events Folder View (Placeholder)
            AlarmsPlaceholderTab(
                theme = cardTheme,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        bottom = contentBottomPadding
                    )
            )
        } else {
            // Diary or Notes Folder View
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
                        if (folderType == FolderType.NOTES) {
                            // Note Folder View
                            NoteTab(
                                notes = uiState.notes,
                                theme = cardTheme,
                                onNoteClick = { selectedNote = it },
                                onBookmarkClick = { note -> viewModel.toggleBookmarkNote(note) },
                                onPinClick = { note -> viewModel.togglePinNote(note) }
                            )
                        } else {
                            // Diary Folder View
                            DiaryTab(
                                entries = uiState.entries,
                                theme = cardTheme,
                                onEntryClick = { selectedEntry = it },
                                onMoodClick = { moodDialogEntry = it }
                            )
                        }
                    }
                    FolderTab.CALENDAR -> {
                        CalendarTab(
                            theme = cardTheme
                        )
                    }
                    FolderTab.EDITOR -> {
                        val activeColorHex = if (folderType == FolderType.NOTES) {
                            editingNote?.colorHex?.takeIf { it.isNotBlank() } ?: uiState.folder?.color
                        } else {
                            editingEntry?.colorHex?.takeIf { it.isNotBlank() } ?: uiState.folder?.color
                        }
                        val editorTheme = rememberTimelineCardPreference(activeColorHex)
                        EditorTab(
                            theme = editorTheme,
                            editorState = editorState
                        )
                    }
                }
            }
        }

        // Note Window Dialog
        selectedNote?.let { note ->
            val latestNote = uiState.notes.find { it.id == note.id } ?: note
            val noteTheme = rememberTimelineCardPreference(latestNote.colorHex ?: uiState.folder?.color)
            NoteWindow(
                noteEntry = latestNote,
                theme = noteTheme,
                onDismiss = { selectedNote = null },
                onEditClick = {
                    editingNote = latestNote
                    editorState.loadNote(latestNote)
                    selectedNote = null
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(FolderTab.EDITOR.ordinal)
                    }
                },
                onColorChange = { newColorHex ->
                    val updated = latestNote.copy(colorHex = newColorHex)
                    viewModel.updateNote(updated)
                    selectedNote = updated
                },
                onDeleteClick = {
                    viewModel.deleteNote(latestNote.id)
                    selectedNote = null
                },
                onPinToggle = {
                    val updated = latestNote.copy(isPinned = !latestNote.isPinned)
                    viewModel.updateNote(updated)
                    selectedNote = updated
                },
                onBookmarkToggle = {
                    val updated = latestNote.copy(isBookmarked = !latestNote.isBookmarked)
                    viewModel.updateNote(updated)
                    selectedNote = updated
                }
            )
        }

        // Diary Window Dialog
        selectedEntry?.let { entry ->
            val latestEntry = uiState.entries.find { it.id == entry.id } ?: entry
            val entryTheme = rememberTimelineCardPreference(latestEntry.colorHex ?: uiState.folder?.color)
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
                },
                onLocationClick = {
                    if (LocationUtil.hasLocationPermission(context)) {
                        coroutineScope.launch {
                            val loc = LocationUtil.resolveLocation(context)
                            val updated = latestEntry.copy(location = loc)
                            viewModel.updateEntry(updated)
                            selectedEntry = updated
                        }
                    } else {
                        pendingLocationRefreshForEntry = latestEntry
                        locationPermissionLauncher.launch(LocationUtil.LOCATION_PERMISSIONS)
                    }
                },
                onDeleteClick = {
                    viewModel.deleteEntry(latestEntry.id)
                    selectedEntry = null
                }
            )
        }

        // Mood Dialog
        moodDialogEntry?.let { entry ->
            val entryTheme = rememberTimelineCardPreference(entry.colorHex ?: uiState.folder?.color)
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
