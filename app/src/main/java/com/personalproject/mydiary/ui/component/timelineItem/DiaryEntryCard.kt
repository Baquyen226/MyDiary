package com.personalproject.mydiary.ui.component.timelineItem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personalproject.mydiary.data.mock.MockDiary
import com.personalproject.mydiary.data.model.preference.BlueSky
import com.personalproject.mydiary.data.model.preference.TimelineCardPreference
import com.personalproject.mydiary.data.model.timelineItem.DiaryEntry
import com.personalproject.mydiary.util.DateUtil

val mockDiary = MockDiary.mockDiaryEntry_1

/**
 * This composable element
 */
@Preview(showBackground = true, backgroundColor = -14330178)
@Composable
fun DiaryEntryCard(
    entry: DiaryEntry = mockDiary,
    theme: TimelineCardPreference = BlueSky.timelineCardPreference
) {
    Card(
        modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, Color.Black),
        colors = CardDefaults.cardColors(containerColor = theme.surfaceColor)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            // Date Section
            Column(
                modifier = Modifier.width(48.dp),
                horizontalAlignment = Alignment.Companion.CenterHorizontally) {
                Text(
                    text = DateUtil.getDayOfMonth(entry.timestamp),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Normal,
                    color = theme.primaryTextColor,
                    lineHeight = 40.sp,
                    fontSize = 40.sp
                )
                Text(
                    text = DateUtil.dayOfWeek(entry.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = theme.primaryTextColor,
                    modifier = Modifier.offset(y = (-1).dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))

            // Content Section
            Column(modifier = Modifier.padding(4.dp)) {
                Text(
                    text = DateUtil.getTime(entry.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = theme.primaryTextColor
                )
                Text(
                    text = entry.title,
                    fontWeight = FontWeight.Bold,
                    color = theme.primaryTextColor
                )
                Text(
                    text = "${entry.content}",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = theme.secondaryTextColor
                )
            }
        }
    }
}