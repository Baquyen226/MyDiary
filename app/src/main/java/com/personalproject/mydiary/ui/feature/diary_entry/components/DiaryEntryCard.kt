package com.personalproject.mydiary.ui.feature.diary_entry.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personalproject.mydiary.R
import com.personalproject.mydiary.core.util.DateUtil
import com.personalproject.mydiary.core.util.toPlainText
import com.personalproject.mydiary.core.util.toPreviewText
import com.personalproject.mydiary.data.mock.MockDiary
import com.personalproject.mydiary.domain.timeline.model.DiaryEntry
import com.personalproject.mydiary.domain.timeline.model.Weather
import com.personalproject.mydiary.ui.feature.diary_entry.getMoodRes
import com.personalproject.mydiary.ui.feature.diary_entry.iconRes
import com.personalproject.mydiary.ui.theme.BlueSky
import com.personalproject.mydiary.ui.theme.TimelineCardPreference
import com.personalproject.mydiary.ui.theme.displayLargeFontFamily
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * A card displaying a summary of a diary entry in timeline view.
 *
 * @param entry The [DiaryEntry] containing timestamp, title, weather, mood, and bookmark status.
 * @param theme The color and style preferences to apply to the card components.
 * @param onClick Triggered when the card is clicked.
 * @param onBookmarkClick Optional callback when the bookmark icon is clicked.
 */
@Preview(showBackground = true, backgroundColor = -14330178)
@Composable
fun DiaryEntryCard(
    entry: DiaryEntry = MockDiary.mockDiaryEntry_3_JP,
    theme: TimelineCardPreference = BlueSky.timelineCardPreference,
    onClick: () -> Unit = {},
    onBookmarkClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        label = "cardPressedScaleAnimation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
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
        colors = CardDefaults.cardColors(containerColor = theme.surfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Date Section: Day number on top, Day of week below
            Column(
                modifier = Modifier.width(54.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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

            Spacer(modifier = Modifier.width(10.dp))

            // Content Section
            Column(modifier = Modifier.padding(2.dp).fillMaxWidth(0.7f)) {
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

                // Attempts to find the first line of text. If not found (only images/recordings), leave it blank.
                val previewText = (entry.bodyPlain.ifEmpty { entry.body }).toPreviewText()

                Text(
                    text = previewText,
                    modifier = Modifier.offset(y = (-1).dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = displayLargeFontFamily,
                    maxLines = 1,
                    color = theme.subtitleTextColor,
                    fontSize = 12.sp
                )
            }

            // Icons Section: Weather -> Mood -> Bookmark
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier.padding(top = 2.dp)
            ) {
                // Weather Icon
                Icon(
                    painter = painterResource(id = entry.weather.iconRes),
                    contentDescription = entry.weather.label,
                    tint = theme.primaryTextColor,
                    modifier = Modifier.size(18.dp)
                )

                // Mood Icon
                val moodRes = entry.moodScore.getMoodRes()
                val moodTint = if (moodRes == 0.getMoodRes())
                    theme.subtitleTextColor else theme.primaryTextColor
                Icon(
                    painter = painterResource(id = moodRes),
                    contentDescription = "Mood",
                    tint = moodTint,
                    modifier = Modifier.size(18.dp)
                )

                // Bookmark Icon
                if (onBookmarkClick != null) {
                    IconButton(
                        onClick = onBookmarkClick,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_diary_bookmark),
                            contentDescription = "Bookmark",
                            tint = theme.primaryTextColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_diary_bookmark),
                        contentDescription = "Bookmark",
                        tint = theme.primaryTextColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
