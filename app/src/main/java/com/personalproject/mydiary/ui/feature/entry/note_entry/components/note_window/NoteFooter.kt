package com.personalproject.mydiary.ui.feature.entry.note_entry.components.note_window

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.personalproject.mydiary.R
import com.personalproject.mydiary.data.mock.MockEntry
import com.personalproject.mydiary.domain.timeline.model.NoteEntry
import com.personalproject.mydiary.ui.feature.common_component.DropdownFontSize
import com.personalproject.mydiary.ui.feature.common_component.DiaryColorPickerDialog
import com.personalproject.mydiary.ui.feature.common_component.parseHexColor
import com.personalproject.mydiary.ui.theme.BlueSky
import com.personalproject.mydiary.ui.theme.TimelineCardPreference

/**
 * Footer for [NoteWindow] displaying three actions: More_horizontal, add_alarm, and delete.
 *
 * @param noteEntry The note entry.
 * @param theme The styling preferences to apply.
 * @param onMoreOptionsClick Callback when more options button is clicked.
 * @param onEditClick Callback to edit note.
 * @param onColorChange Callback when a color hex is selected.
 * @param onAddAlarmClick Callback when add_alarm is clicked.
 * @param onDeleteClick Callback when delete is clicked.
 * @param onPinToggle Callback to toggle pinned status.
 * @param onBookmarkToggle Callback to toggle bookmark status.
 */
@Composable
@Preview
fun NoteFooter(
    noteEntry: NoteEntry = MockEntry.mockNoteEntry_1,
    theme: TimelineCardPreference = BlueSky.timelineCardPreference,
    onMoreOptionsClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onColorChange: (String) -> Unit = {},
    onAddAlarmClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onPinToggle: () -> Unit = {},
    onBookmarkToggle: () -> Unit = {}
) {
    var showMoreMenu by remember { mutableStateOf(false) }
    var showColorDialog by remember { mutableStateOf(false) }

    val currentColor = remember(noteEntry.colorHex, theme.primaryTextColor) {
        parseHexColor(noteEntry.colorHex) ?: theme.primaryTextColor
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(theme.primaryTextColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. More_horizontal Button
            Box {
                IconButton(onClick = {
                    onMoreOptionsClick()
                    showMoreMenu = true
                }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_ui_more_horizontal_24dp),
                        contentDescription = "More Options",
                        tint = Color.White
                    )
                }

                DropdownMenu(
                    expanded = showMoreMenu,
                    onDismissRequest = { showMoreMenu = false },
                    shape = RoundedCornerShape(14.dp),
                    containerColor = theme.surfaceColor,
                    shadowElevation = 6.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.width(195.dp)
                ) {
                    val itemModifier = Modifier
                        .padding(horizontal = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                    val itemPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)

                    // Edit Note
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Edit Note",
                                color = theme.secondaryTextColor,
                                fontSize = DropdownFontSize,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_folderview_type_notes),
                                contentDescription = "Edit Note",
                                modifier = Modifier.size(19.dp),
                                tint = theme.primaryTextColor
                            )
                        },
                        contentPadding = itemPadding,
                        modifier = itemModifier,
                        onClick = {
                            showMoreMenu = false
                            onEditClick()
                        }
                    )

                    // Change Color
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Color",
                                color = theme.secondaryTextColor,
                                fontSize = DropdownFontSize,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_diary_color),
                                contentDescription = "Change Color",
                                modifier = Modifier.size(19.dp),
                                tint = theme.primaryTextColor
                            )
                        },
                        trailingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(currentColor)
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                            )
                        },
                        contentPadding = itemPadding,
                        modifier = itemModifier,
                        onClick = {
                            showMoreMenu = false
                            showColorDialog = true
                        }
                    )

                    // Pin / Unpin Note
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (noteEntry.isPinned) "Unpin Note" else "Pin Note",
                                color = theme.secondaryTextColor,
                                fontSize = DropdownFontSize,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (noteEntry.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                contentDescription = "Pin Note",
                                modifier = Modifier.size(19.dp),
                                tint = theme.primaryTextColor
                            )
                        },
                        contentPadding = itemPadding,
                        modifier = itemModifier,
                        onClick = {
                            showMoreMenu = false
                            onPinToggle()
                        }
                    )

                    // Bookmark / Remove Bookmark
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (noteEntry.isBookmarked) "Remove Bookmark" else "Bookmark",
                                color = theme.secondaryTextColor,
                                fontSize = DropdownFontSize,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (noteEntry.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Bookmark",
                                modifier = Modifier.size(19.dp),
                                tint = theme.primaryTextColor
                            )
                        },
                        contentPadding = itemPadding,
                        modifier = itemModifier,
                        onClick = {
                            showMoreMenu = false
                            onBookmarkToggle()
                        }
                    )
                }
            }

            // 2. add_alarm Button
            IconButton(onClick = onAddAlarmClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_ui_add_alarm),
                    contentDescription = "Add Alarm",
                    tint = Color.White
                )
            }

            // 3. delete Button
            IconButton(onClick = onDeleteClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_ui_delete),
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        }
    }

    if (showColorDialog) {
        DiaryColorPickerDialog(
            initialColorHex = noteEntry.colorHex,
            fallbackColor = theme.primaryTextColor,
            onDismiss = { showColorDialog = false },
            onColorSelected = { newHex ->
                showColorDialog = false
                onColorChange(newHex)
            }
        )
    }
}
