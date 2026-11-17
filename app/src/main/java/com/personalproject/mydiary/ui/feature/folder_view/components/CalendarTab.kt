package com.personalproject.mydiary.ui.feature.folder_view.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.personalproject.mydiary.ui.theme.TimelineCardPreference

@Composable
fun CalendarTab(
    theme: TimelineCardPreference,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Calendar View",
                style = MaterialTheme.typography.titleMedium,
                color = theme.primaryTextColor
            )
            Text(
                text = "not implemented yet lol",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}
