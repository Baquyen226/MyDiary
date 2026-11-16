package com.personalproject.mydiary.ui.feature.diary_entry.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personalproject.mydiary.data.mock.MockDiary
import com.personalproject.mydiary.domain.timeline.model.DiaryEntry
import com.personalproject.mydiary.ui.theme.displayLargeFontFamily
import com.personalproject.mydiary.core.util.DateUtil
import com.personalproject.mydiary.ui.theme.BlueSky
import com.personalproject.mydiary.ui.theme.TimelineCardPreference

val mockDiary = MockDiary.mockDiaryEntry_3_JP


/**
 * A simplified card display a diary entry.
 *
 * @param entry The [DiaryEntry] data object containing the timestamp, title, and content.
 * @param theme The color and style preferences to apply to the window components.
 */
@Preview(showBackground = true, backgroundColor = -14330178)
@Composable
fun DiaryEntryCard(
    entry: DiaryEntry = mockDiary,
    theme: TimelineCardPreference = BlueSky.timelineCardPreference,
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "cardPressedScaleAnimation"
    )

    Card(
        modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        onClick = onClick,
                        indication = null
                    ),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, Color.Black),
        colors = CardDefaults.cardColors(containerColor = theme.surfaceColor)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            // Date Section
            Column(
                modifier = Modifier.width(54.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = DateUtil.getDayOfMonth(entry.timestamp),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Light,
                    fontFamily = displayLargeFontFamily,
                    color = theme.primaryTextColor,
                    lineHeight = 46.sp,
                    fontSize = 36.sp
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
            Column(modifier = Modifier.padding(2.dp).width(196.dp)) {
                Text(
                    text = DateUtil.getTime(entry.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = theme.primaryTextColor
                )
                Text(
                    text = entry.title,
                    modifier = Modifier.offset(y = (-2).dp),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    fontFamily = displayLargeFontFamily,
                    color = theme.primaryTextColor
                )
                Text(
                    text = "${entry.content}",
                    modifier = Modifier.offset(y = (-1).dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = displayLargeFontFamily,
                    maxLines = 1,
                    color = theme.subtitleTextColor,
                    fontSize = 12.sp
                )
            }
        }
    }
}