package com.personalproject.mydiary.ui.feature.folder_view.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personalproject.mydiary.domain.timeline.model.NoteEntry
import com.personalproject.mydiary.ui.feature.entry.note_entry.components.note_card.NoteCard
import com.personalproject.mydiary.ui.theme.TimelineCardPreference
import com.personalproject.mydiary.ui.theme.displayLargeFontFamily
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId

/**
 * Tab displaying the list of [NoteEntry] cards for note-type folders.
 *
 * Notes are sorted with pinned items first, then chronologically by month.
 *
 * @param notes The list of notes in the folder.
 * @param theme The styling preferences to apply.
 * @param onNoteClick Callback when a note card is clicked.
 * @param onBookmarkClick Callback when the bookmark icon on a card is clicked.
 * @param onPinClick Callback when the pin icon on a card is clicked.
 */
@Composable
fun NoteTab(
    notes: List<NoteEntry>,
    theme: TimelineCardPreference,
    onNoteClick: (NoteEntry) -> Unit,
    onBookmarkClick: (NoteEntry) -> Unit = {},
    onPinClick: (NoteEntry) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        if (notes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No notes yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            val groupedNotes = remember(notes) {
                notes
                    .sortedWith(
                        compareByDescending<NoteEntry> { it.isPinned }
                            .thenByDescending { it.timestamp ?: it.createdAt }
                    )
                    .groupBy { note ->
                        val target = note.timestamp ?: note.createdAt
                        YearMonth.from(target.atZone(ZoneId.systemDefault()))
                    }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                groupedNotes.forEach { (yearMonth, monthNotes) ->
                    if (yearMonth != null) {
                        item(key = "header_${yearMonth}") {
                            Text(
                                text = yearMonth.monthValue.toString(),
                                style = MaterialTheme.typography.displayMedium,
                                fontFamily = displayLargeFontFamily,
                                fontWeight = FontWeight.Light,
                                fontSize = 42.sp,
                                color = theme.primaryTextColor.copy(alpha = 0.65f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            )
                        }
                    }

                    items(monthNotes, key = { it.id }) { note ->
                        NoteCard(
                            noteEntry = note,
                            theme = theme,
                            onClick = { onNoteClick(note) },
                            onBookmarkClick = { onBookmarkClick(note) },
                            onPinClick = { onPinClick(note) }
                        )
                    }
                }
            }
        }
    }
}
