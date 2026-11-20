package com.personalproject.mydiary.ui.feature.folder_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.personalproject.mydiary.R
import com.personalproject.mydiary.ui.feature.common_component.BottomBarModifier
import com.personalproject.mydiary.ui.theme.TimelineCardPreference

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    theme: TimelineCardPreference? = null,
    onSearchClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val isDark = isSystemInDarkTheme()
    val barColor = MaterialTheme.colorScheme.surface
    val defaultIconColor = if (isDark) MaterialTheme.colorScheme.primary else Color(0xFF4A5D74)
    val defaultPillColor = if (isDark) MaterialTheme.colorScheme.surfaceContainerHigh else Color(0xFF4A5D74)
    val iconColor = theme?.primaryTextColor ?: defaultIconColor
    val pillColor = theme?.primaryTextColor ?: defaultPillColor

    Surface(
        color = barColor,
        modifier = modifier.fillMaxWidth()
    ) {
        Column() {
            Spacer(Modifier.padding(top = 4.dp))

            Row(
                modifier = BottomBarModifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.ui_search),
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
                    contentDescription = stringResource(R.string.ui_settings),
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
