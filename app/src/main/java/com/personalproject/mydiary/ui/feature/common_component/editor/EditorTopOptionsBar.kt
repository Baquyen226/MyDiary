package com.personalproject.mydiary.ui.feature.common_component.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personalproject.mydiary.R
import com.personalproject.mydiary.ui.theme.TimelineCardPreference
import com.personalproject.mydiary.ui.theme.displayLargeFontFamily

/**
 * Top section of the editor containing:
 * 1. Title input
 * 2. Feature tool buttons (Format toggle, Line separator, Attach photo, Camera)
 */
@Composable
fun EditorTopOptionsBar(
    title: String,
    onTitleChange: (String) -> Unit,
    showFormattingBar: Boolean,
    onToggleFormattingBar: () -> Unit,
    onInsertSeparator: () -> Unit,
    onPickImage: () -> Unit,
    onCameraClick: () -> Unit,
    theme: TimelineCardPreference,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        // 1. Title Field
        TextField(
            value = title,
            onValueChange = onTitleChange,
            placeholder = {
                Text(
                    text = "Title",
                    fontFamily = displayLargeFontFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            },
            singleLine = true,
            textStyle = TextStyle(
                fontFamily = displayLargeFontFamily,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = theme.primaryTextColor
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // 2. Options Bar: [Format], [Line Separator], [Attach], [Camera]
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Formatting Toggle Button
            IconButton(
                onClick = onToggleFormattingBar,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (showFormattingBar) theme.primaryTextColor.copy(alpha = 0.15f) else Color.Transparent,
                    contentColor = if (showFormattingBar) theme.primaryTextColor else Color.Gray
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_editor_format),
                    contentDescription = "Formatting Options",
                    modifier = Modifier.size(22.dp)
                )
            }

            // Line Separator Button
            IconButton(onClick = onInsertSeparator) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_editor_format_line_break),
                    contentDescription = "Insert Line Separator",
                    tint = Color.Gray,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Attach Image Button
            IconButton(onClick = onPickImage) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_diary_attach),
                    contentDescription = "Attach Image",
                    tint = Color.Gray,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Camera Shortcut Button
            IconButton(onClick = onCameraClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_ui_photo_camera_24dp),
                    contentDescription = "Camera",
                    tint = Color.Gray,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
