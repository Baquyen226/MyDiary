package com.personalproject.mydiary.ui.feature.common_component.editor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.personalproject.mydiary.R
import com.personalproject.mydiary.ui.theme.TimelineCardPreference
import com.personalproject.mydiary.ui.theme.displayLargeFontFamily

/**
 * Top section of the editor containing:
 * 1. Title input
 * 2. Feature tool buttons (Format toggle, Line separator, Attach photo, Camera, Ribbon-style Block Selection)
 * 3. Anchored Ribbon flyout panel with 4 distinct tiles to edit a block.
 */
@Composable
fun EditorTopOptionsBar(
    modifier: Modifier = Modifier,
    title: String,
    onTitleChange: (String) -> Unit,
    showFormattingBar: Boolean,
    onToggleFormattingBar: () -> Unit,
    onInsertSeparator: () -> Unit,
    onPickImage: () -> Unit,
    onCameraClick: () -> Unit,
    onSelectCurrentBlock: () -> Unit,
    onExtendSelectionUp: () -> Unit,
    onExtendSelectionDown: () -> Unit,
    onSelectAllBlocks: () -> Unit,
    hasSelection: Boolean = false,
    isAllBlocksSelected: Boolean = false,
    theme: TimelineCardPreference
) {
    var showSelectionBox by remember { mutableStateOf(false) }

    LaunchedEffect(hasSelection) {
        if (hasSelection) {
            showSelectionBox = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        // 1. Title Input Field
        TextField(
            value = title,
            onValueChange = onTitleChange,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.ui_folder_view_diary_title_prompt),
                    style = TextStyle(
                        fontFamily = displayLargeFontFamily,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                )
            },
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

        // 2. Options Bar: [Format], [Line Separator], [Attach], [Camera], [Anchored Selection Tool Ë…]
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
                    contentColor = if (showFormattingBar) theme.primaryTextColor else MaterialTheme.colorScheme.onSurfaceVariant
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
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Attach Image Button
            IconButton(onClick = onPickImage) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_diary_attach),
                    contentDescription = "Attach Image",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Camera Shortcut Button
            IconButton(onClick = onCameraClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_ui_photo_camera_24dp),
                    contentDescription = "Camera",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Anchored Selection Tool Button: [Icon Ë…] with Word-style anchored flyout
            Box {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (showSelectionBox) MaterialTheme.colorScheme.surfaceContainerHigh else Color.Transparent,
                    border = BorderStroke(
                        1.dp,
                        if (showSelectionBox) MaterialTheme.colorScheme.outlineVariant
                        else if (hasSelection) theme.primaryTextColor.copy(alpha = 0.3f)
                        else Color.Transparent
                    ),
                    onClick = { showSelectionBox = !showSelectionBox }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_editor_select_all_block),
                            contentDescription = stringResource(R.string.ui_editor_block_selection),
                            tint = if (showSelectionBox || hasSelection) theme.primaryTextColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = if (showSelectionBox || hasSelection) theme.primaryTextColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Ribbon Flyout Panel anchored directly underneath the button
                if (showSelectionBox) {
                    val offsetY = with(LocalDensity.current) { 42.dp.roundToPx() }
                    Popup(
                        alignment = Alignment.TopEnd,
                        offset = IntOffset(x = 0, y = offsetY),
                        onDismissRequest = { showSelectionBox = false },
                        properties = PopupProperties(
                            focusable = false,
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true
                        )
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
                            shadowElevation = 6.dp,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val isThisBlockSelected = hasSelection && !isAllBlocksSelected

                                SelectionFlyoutTile(
                                    isSelected = isThisBlockSelected,
                                    activeColor = theme.primaryTextColor,
                                    onClick = onSelectCurrentBlock
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_editor_select_this_block),
                                        contentDescription = stringResource(R.string.ui_editor_select_this_block),
                                        modifier = Modifier.size(20.dp),
                                        tint = if (isThisBlockSelected) theme.primaryTextColor else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                SelectionFlyoutTile(
                                    onClick = onExtendSelectionUp
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = stringResource(R.string.ui_editor_extend_up),
                                        modifier = Modifier.size(22.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                SelectionFlyoutTile(
                                    onClick = onExtendSelectionDown
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = stringResource(R.string.ui_editor_extend_down),
                                        modifier = Modifier.size(22.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                SelectionFlyoutTile(
                                    onClick = onSelectAllBlocks
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_editor_select_all_block),
                                        contentDescription = stringResource(R.string.ui_editor_select_all_blocks),
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectionFlyoutTile(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    activeColor: Color = Color.Transparent,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (isSelected) activeColor.copy(alpha = 0.12f) else Color.Transparent,
        border = BorderStroke(
            1.dp,
            if (isSelected) activeColor.copy(alpha = 0.35f) else Color.Transparent
        ),
        onClick = onClick,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
    }
}
