package com.personalproject.mydiary.ui.screen.diaryfolder

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DiaryFolderScreen(
    viewModel: DiaryFolderViewModel,
    modifier: Modifier = Modifier
) {
    val tabs = listOf("Entries", "Calendar", "Diary")
    var selectedIndex by remember { mutableIntStateOf(0) }

    Surface() {
        Column() {
            DiaryTabs()
            DiaryBody()
            DiaryFooter()
        }
    }
}

@Composable
fun DiaryTabs() {

}

@Composable
fun DiaryBody() {}

@Composable
fun DiaryFooter() {}