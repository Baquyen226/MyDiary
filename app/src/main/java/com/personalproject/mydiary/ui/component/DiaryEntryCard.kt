package com.personalproject.mydiary.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.personalproject.mydiary.data.mock.MockDiary
import com.personalproject.mydiary.data.model.timelineItem.DiaryEntry
import com.personalproject.mydiary.util.DateUtil

class DiaryEntryCard {
    val mockDiary = MockDiary.mockDiaryEntry_1

    @Preview(showBackground = true)
    @Composable
    fun TimelineCard(entry: DiaryEntry = mockDiary) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.Black),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                // Date Section
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = DateUtil.getDayOfMonth(entry.timestamp), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text(text = DateUtil.dayOfWeek(entry.timestamp), style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Content Section
                Column {
                    Text(text = DateUtil.getTime(entry.timestamp), style = MaterialTheme.typography.labelSmall)
                    Text(text = entry.title, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${entry.description}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}