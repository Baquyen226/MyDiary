package com.personalproject.mydiary.ui.feature.folder_view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personalproject.mydiary.ui.feature.common_component.SegmentedTabs
import com.personalproject.mydiary.ui.feature.folder_view.FolderTab
import com.personalproject.mydiary.ui.theme.displayLargeFontFamily

@Composable
fun HeaderSection(
    title: String,
    selectedTab: FolderTab,
    onTabSelect: (FolderTab) -> Unit,
    onBackClick: () -> Unit,
    tintColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                SegmentedTabs(
                    options = FolderTab.entries.map { it.label },
                    selectedIndex = selectedTab.ordinal,
                    onSelect = { index -> onTabSelect(FolderTab.entries[index]) },
                    tintColor = tintColor,
                    modifier = Modifier.fillMaxWidth(0.75f)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = displayLargeFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = tintColor
        )
    }
}
