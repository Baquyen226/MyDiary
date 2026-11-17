package com.personalproject.mydiary.ui.feature.folder_view.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personalproject.mydiary.ui.feature.folder_view.FolderTab
import com.personalproject.mydiary.ui.theme.TimelineCardPreference
import com.personalproject.mydiary.ui.theme.displayLargeFontFamily

@Composable
fun FolderViewBottomBar(
    currentTab: FolderTab,
    theme: TimelineCardPreference,
    entriesCount: Int,
    onNewEntryClick: () -> Unit,
    onDiscardEditorClick: () -> Unit,
    onSaveEditorClick: () -> Unit,
    onTodayClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        color = theme.primaryTextColor,
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .height(60.dp)
        ) {
            AnimatedContent(
                targetState = currentTab,
                label = "FolderViewBottomBarAnimation"
            ) { tab ->
                when (tab) {
                    FolderTab.ENTRIES -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { /* Menu */ }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(Modifier.width(24.dp))

                            IconButton(onClick = onNewEntryClick) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "New Entry",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(Modifier.width(24.dp))

                            IconButton(onClick = { /* Camera */ }) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Camera",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val countText = if (entriesCount == 1) "1  entry" else "$entriesCount  entry"
                                Text(
                                    text = countText,
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = displayLargeFontFamily
                                )
                            }
                        }
                    }

                    FolderTab.CALENDAR -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = onTodayClick,
                                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                            ) {
                                Text("Today")
                            }
                        }
                    }

                    FolderTab.EDITOR -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(onClick = onDiscardEditorClick) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Discard",
                                    tint = Color.White
                                )
                            }

                            Text(
                                text = "New Entry",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White
                            )

                            IconButton(onClick = onSaveEditorClick) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Save",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
