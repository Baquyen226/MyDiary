package com.personalproject.mydiary.ui.feature.diary_entry.components.diary_window

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personalproject.mydiary.R
import com.personalproject.mydiary.data.mock.MockDiary
import com.personalproject.mydiary.domain.timeline.model.DiaryEntry
import com.personalproject.mydiary.ui.feature.diary_entry.getMoodRes
import com.personalproject.mydiary.ui.theme.BlueSky
import com.personalproject.mydiary.ui.theme.TimelineCardPreference


@Composable
fun DiaryFooter(
    entry: DiaryEntry = MockDiary.mockDiaryEntry_1,
    theme: TimelineCardPreference = BlueSky.timelineCardPreference,
    onMoreOptionsClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onColorChange: (String) -> Unit = {},
    onMoodChange: (Int) -> Unit = {},
    onLocationClick: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    onMusicClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    var showMoreMenu by remember { mutableStateOf(false) }
    var showColorDialog by remember { mutableStateOf(false) }
    var showMoodDialog by remember { mutableStateOf(false) }

    val currentColor = remember(entry.colorHex, theme.primaryTextColor) {
        parseHexColor(entry.colorHex) ?: theme.primaryTextColor
    }
    val displayHex = entry.colorHex ?: "#1E81B0"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(theme.primaryTextColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // More Options
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
                    modifier = Modifier
                        .background(Color.White)
                        .widthIn(min = 230.dp)
                ) {
                    // 1. Edit Diary - enters DiaryEditor with it
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Edit Diary",
                                color = Color(0xFF212121),
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_folderview_type_diary),
                                contentDescription = "Edit Diary",
                                modifier = Modifier.size(20.dp),
                                tint = theme.primaryTextColor
                            )
                        },
                        onClick = {
                            showMoreMenu = false
                            onEditClick()
                        }
                    )

                    // 2. Change Color Hex - displays current color on the right, displays its preview color
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Color",
                                color = Color(0xFF212121),
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_diary_color),
                                contentDescription = "Color",
                                modifier = Modifier.size(20.dp),
                                tint = theme.primaryTextColor
                            )
                        },
                        trailingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(currentColor)
                                        .border(1.dp, Color.LightGray.copy(alpha = 0.5f), CircleShape)
                                )
                            }
                        },
                        onClick = {
                            showMoreMenu = false
                            showColorDialog = true
                        }
                    )

                    // 3. Change Mood
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Mood",
                                color = Color(0xFF212121),
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_diary_mood_happy1),
                                contentDescription = "Mood",
                                modifier = Modifier.size(20.dp),
                                tint = theme.primaryTextColor
                            )
                        },
                        trailingIcon = {
                            val moodRes = entry.moodScore.getMoodRes()
                            Icon(
                                painter = painterResource(id = moodRes),
                                contentDescription = "Current Mood",
                                modifier = Modifier.size(20.dp),
                                tint = if (entry.moodScore == 0) Color.Gray else theme.primaryTextColor
                            )
                        },
                        onClick = {
                            showMoreMenu = false
                            showMoodDialog = true
                        }
                    )
                }
            }

            // Location
            IconButton(onClick = onLocationClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_ui_location_24dp),
                    contentDescription = "Location",
                    tint = Color.White
                )
            }

            // Camera
            IconButton(onClick = onCameraClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_ui_photo_camera_24dp),
                    contentDescription = "Camera",
                    tint = Color.White
                )
            }

            // Music
            IconButton(onClick = onMusicClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_ui_music_note_24dp),
                    contentDescription = "Music",
                    tint = Color.White
                )
            }

            // Delete
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
            initialColorHex = entry.colorHex,
            fallbackColor = theme.primaryTextColor,
            onDismiss = { showColorDialog = false },
            onColorSelected = { newHex ->
                showColorDialog = false
                onColorChange(newHex)
            }
        )
    }

    if (showMoodDialog) {
        MoodPickerDialog(
            currentMood = entry.moodScore,
            theme = theme,
            onDismiss = { showMoodDialog = false },
            onMoodSelected = { newMood ->
                showMoodDialog = false
                onMoodChange(newMood)
            }
        )
    }
}
