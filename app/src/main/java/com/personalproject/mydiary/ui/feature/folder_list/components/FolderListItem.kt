package com.personalproject.mydiary.ui.feature.folder_list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.personalproject.mydiary.R
import com.personalproject.mydiary.domain.folder.model.FolderType
import com.personalproject.mydiary.ui.feature.folder_list.FolderSummary
import com.personalproject.mydiary.ui.theme.TimelineCardPreference

@Composable
fun FolderListItem(
    modifier: Modifier = Modifier,
    summary: FolderSummary,
    theme: TimelineCardPreference? = null,
    themeColor: Color? = null,
    onClick: () -> Unit
) {
    val folder = summary.folder
    val iconRes = when (folder.type) {
        FolderType.CONTACTS -> R.drawable.ic_folderview_type_contact
        FolderType.DIARY -> R.drawable.ic_folderview_type_diary
        FolderType.EVENT -> R.drawable.ic_folderview_type_event
        FolderType.NOTES -> R.drawable.ic_folderview_type_notes
    }
    
    val isDark = isSystemInDarkTheme()
    val defaultColor = if (isDark) MaterialTheme.colorScheme.onSurface else Color.DarkGray
    val textColor = remember(folder.color, isDark) {
        folder.color?.let { hex ->
            runCatching {
                val base = Color(hex.toColorInt())
                if (isDark) {
                    val hsv = FloatArray(3)
                    android.graphics.Color.colorToHSV(base.toArgb(), hsv)
                    hsv[2] = hsv[2].coerceAtLeast(0.78f)
                    Color(android.graphics.Color.HSVToColor(hsv))
                } else {
                    base
                }
            }.getOrNull()
        } ?: defaultColor
    }

    Column (
        modifier = Modifier.padding(start = 24.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 28.dp)
                .padding(start = 4.dp, end = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = folder.name,
                tint = textColor,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(18.dp))

            Text(
                text = folder.name,
                style = MaterialTheme.typography.titleMedium,
                color = textColor,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = summary.itemCount.toString(),
                color = textColor,
                fontSize = 12.sp,
                modifier = Modifier.padding(end = 4.dp)
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_ui_arrow),
                contentDescription = null,
                tint = textColor,
                modifier = Modifier
                    .size(12.dp)
                    .rotate(180f)
            )
        }

        val defaultDividerColor = if (isDark) MaterialTheme.colorScheme.primary else Color(0xFF4A5D74)
        val dividerColor = themeColor ?: theme?.primaryTextColor ?: defaultDividerColor

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 0.5.dp,
            color = dividerColor
        )
    }
}
