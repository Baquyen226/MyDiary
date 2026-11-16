package com.personalproject.mydiary.ui.feature.folder_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.personalproject.mydiary.ui.feature.folder_list.components.BottomBar
import com.personalproject.mydiary.ui.feature.folder_list.components.FolderListItem
import com.personalproject.mydiary.ui.feature.folder_list.components.ProfileHeader
import com.personalproject.mydiary.ui.feature.folder_list.components.TagsSection

@Composable
fun FolderListScreen(
    viewModel: FolderListViewModel,
    onFolderClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomBar()
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                ProfileHeader(profile = uiState.profile)

                // TagsSection(tags = uiState.tags)

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(uiState.folders, key = { it.folder.id }) { summary ->
                        FolderListItem(
                            summary = summary,
                            onClick = { onFolderClick(summary.folder.id) }
                        )
                    }
                }
            }
        }
    }
}
