package com.personalproject.mydiary.ui.feature.folder_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.personalproject.mydiary.ui.feature.folder_list.components.BottomBar
import com.personalproject.mydiary.ui.feature.folder_list.components.FolderListItem
import com.personalproject.mydiary.ui.feature.folder_list.components.ProfileDialog
import com.personalproject.mydiary.ui.feature.folder_list.components.ProfileHeader
import com.personalproject.mydiary.ui.theme.rememberTimelineCardPreference

@Composable
fun FolderListScreen(
    modifier: Modifier = Modifier,
    viewModel: FolderListViewModel,
    onFolderClick: (String) -> Unit,
    onNavigateToProfileEdit: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cardTheme = rememberTimelineCardPreference(uiState.profile?.themeColor)
    var showProfileDialog by remember { mutableStateOf(false) }

    if (showProfileDialog) {
        ProfileDialog(
            profile = uiState.profile,
            onDismissRequest = { showProfileDialog = false },
            onEditClick = {
                showProfileDialog = false
                onNavigateToProfileEdit()
            }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            ProfileHeader(
                profile = uiState.profile,
                onAvatarClick = { showProfileDialog = true }
            )
        },
        bottomBar = {
            BottomBar(theme = cardTheme)
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(uiState.folders, key = { it.folder.id }) { summary ->
                    FolderListItem(
                        summary = summary,
                        theme = cardTheme,
                        onClick = { onFolderClick(summary.folder.id) }
                    )
                }
            }
        }
    }
}
