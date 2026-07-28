package com.personalproject.mydiary.ui.feature_folder_list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.personalproject.mydiary.ui.feature_folder_list.components.BottomBar
import com.personalproject.mydiary.ui.feature_folder_list.components.FolderListItem
import com.personalproject.mydiary.ui.feature_folder_list.components.ProfileHeader
import com.personalproject.mydiary.ui.feature_folder_list.components.TagsSection

@Composable
fun FolderListScreen(
    viewModel: FolderListViewModel,
    onFolderClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        bottomBar = {
            BottomBar()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ProfileHeader(profile = uiState.profile)
            
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(uiState.folders) { summary ->
                    FolderListItem(
                        summary = summary,
                        onClick = { onFolderClick(summary.folder.uuid) }
                    )
                }
            }
        }
    }
}
