package com.personalproject.mydiary.ui.feature.folder_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.personalproject.mydiary.ui.feature.common_component.BottomBarModifier

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val isDark = isSystemInDarkTheme()
    val barColor = MaterialTheme.colorScheme.surface
    val iconColor = if (isDark) MaterialTheme.colorScheme.primary else Color(0xFF4A5D74)
    val pillColor = if (isDark) MaterialTheme.colorScheme.surfaceContainerHigh else Color(0xFF4A5D74)

    Surface(
        color = barColor,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = BottomBarModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(pillColor)
            )

            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
