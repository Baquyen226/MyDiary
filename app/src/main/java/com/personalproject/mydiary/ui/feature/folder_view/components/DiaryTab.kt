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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personalproject.mydiary.domain.timeline.model.DiaryEntry
import com.personalproject.mydiary.ui.feature.diary_entry.components.DiaryEntryCard
import com.personalproject.mydiary.ui.theme.TimelineCardPreference
import com.personalproject.mydiary.ui.theme.displayLargeFontFamily
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId

@Composable
fun DiaryTab(
    entries: List<DiaryEntry>,
    theme: TimelineCardPreference,
    onEntryClick: (DiaryEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        if (entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No entries yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        } else {
            val groupedEntries = remember(entries) {
                entries
                    .sortedByDescending { it.timestamp ?: Instant.MIN }
                    .groupBy { entry ->
                        entry.timestamp?.let {
                            YearMonth.from(it.atZone(ZoneId.systemDefault()))
                        }
                    }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                groupedEntries.forEach { (yearMonth, monthEntries) ->
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

                    items(monthEntries, key = { it.id }) { entry ->
                        DiaryEntryCard(
                            entry = entry,
                            theme = theme,
                            onClick = { onEntryClick(entry) }
                        )
                    }
                }
            }
        }
    }
}
