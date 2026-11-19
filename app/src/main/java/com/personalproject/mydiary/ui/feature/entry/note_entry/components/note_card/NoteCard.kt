package com.personalproject.mydiary.ui.feature.entry.note_entry.components.note_card

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personalproject.mydiary.R
import com.personalproject.mydiary.core.util.DateUtil
import com.personalproject.mydiary.data.mock.MockEntry
import com.personalproject.mydiary.domain.timeline.model.NoteEntry
import com.personalproject.mydiary.ui.theme.BlueSky
import com.personalproject.mydiary.ui.theme.TimelineCardPreference
import com.personalproject.mydiary.ui.theme.colorHexToCardPreference
import com.personalproject.mydiary.ui.theme.displayLargeFontFamily

/**
 * A card displaying a summary of a note entry in timeline or folder view.
 *
 * @param modifier Modifier for styling and layout.
 * @param noteEntry The [NoteEntry] containing timestamp, title, body content, and bookmark status.
 * @param theme The color and style preferences to apply to the card components.
 * @param onClick Triggered when the card is clicked.
 * @param onBookmarkClick Optional callback when the bookmark icon is clicked.
 * @param onPinClick Optional callback when the pin icon is clicked.
 */
@Composable
@Preview
fun NoteCard(
    modifier: Modifier = Modifier,
    noteEntry: NoteEntry = MockEntry.mockNoteEntry_1,
    theme: TimelineCardPreference = BlueSky.timelineCardPreference,
    onClick: () -> Unit = {},
    onBookmarkClick: (() -> Unit)? = null,
    onPinClick: (() -> Unit)? = null
) {
    val isDark = isSystemInDarkTheme()
    val effectiveTheme = remember(noteEntry.colorHex, theme, isDark) {
        noteEntry.colorHex?.takeIf { it.isNotBlank() }?.let { colorHexToCardPreference(it, isDark) } ?: theme
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        label = "noteCardPressedScaleAnimation"
    )

    val targetTimestamp = noteEntry.timestamp ?: noteEntry.createdAt

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .heightIn(min = 74.dp)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
            .clickable(
                interactionSource = interactionSource,
                onClick = onClick,
                indication = null
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = effectiveTheme.surfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .padding(start = 16.dp, end = 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Date Section: Day number on top, Day of week below
            Column(
                modifier = Modifier.width(54.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = DateUtil.getDayOfMonth(targetTimestamp),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Light,
                    fontFamily = displayLargeFontFamily,
                    color = effectiveTheme.primaryTextColor,
                    lineHeight = 46.sp,
                    fontSize = 36.sp
                )
                Text(
                    text = DateUtil.dayOfWeek(targetTimestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = effectiveTheme.primaryTextColor,
                    modifier = Modifier.offset(y = (-1).dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Content Section
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(alignment = Alignment.CenterVertically)
                    .padding(2.dp),

            ) {
                Text(
                    text = noteEntry.title,
                    modifier = Modifier.offset(y = (-2).dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    maxLines = 1,
                    fontFamily = displayLargeFontFamily,
                    color = effectiveTheme.primaryTextColor
                )
            }

            // Right Section: Bookmark & Optional Pin/Attachment status
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
            ) {
                // Pin Indicator
                if (noteEntry.isPinned) {
                    if (onPinClick != null) {
                        IconButton(
                            onClick = onPinClick,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PushPin,
                                contentDescription = "Pinned Note",
                                tint = effectiveTheme.primaryTextColor,
                                modifier = Modifier
                                    .size(18.dp)
                                    .rotate(30f)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Filled.PushPin,
                            contentDescription = "Pinned Note",
                            tint = effectiveTheme.primaryTextColor,
                            modifier = Modifier
                                .size(18.dp)
                                .rotate(30f)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Bookmark Icon (filled if bookmarked, outline if not)
                    val bookmarkIcon = if (noteEntry.isBookmarked) {
                        Icons.Filled.Bookmark
                    } else {
                        Icons.Outlined.BookmarkBorder
                    }
                    val bookmarkTint = if (noteEntry.isBookmarked) {
                        effectiveTheme.primaryTextColor
                    } else {
                        effectiveTheme.primaryTextColor.copy(alpha = 0.5f)
                    }

                    if (onBookmarkClick != null) {
                        IconButton(
                            onClick = onBookmarkClick,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = bookmarkIcon,
                                contentDescription = if (noteEntry.isBookmarked) "Bookmarked" else "Not Bookmarked",
                                tint = bookmarkTint,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = bookmarkIcon,
                            contentDescription = if (noteEntry.isBookmarked) "Bookmarked" else "Not Bookmarked",
                            tint = bookmarkTint,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (noteEntry.imageUris.isNotEmpty()) {
                        Spacer(Modifier.size(10.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_diary_attach),
                            contentDescription = "Has Attachment",
                            tint = effectiveTheme.primaryTextColor,
                            modifier = Modifier
                                .size(22.dp)
                                .rotate(30f)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = -14330178)
@Composable
private fun NoteCardPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NoteCard(
            noteEntry = MockEntry.mockNoteEntry_1
        )
        NoteCard(
            noteEntry = MockEntry.mockNoteEntry_2.copy(isBookmarked = true)
        )
    }
}
