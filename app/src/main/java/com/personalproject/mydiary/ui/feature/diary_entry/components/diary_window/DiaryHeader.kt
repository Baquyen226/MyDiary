package com.personalproject.mydiary.ui.feature.diary_entry.components.diary_window

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personalproject.mydiary.R
import com.personalproject.mydiary.core.util.DateUtil
import com.personalproject.mydiary.domain.timeline.model.DiaryEntry
import com.personalproject.mydiary.domain.timeline.model.Weather
import com.personalproject.mydiary.ui.feature.diary_entry.iconRes
import com.personalproject.mydiary.ui.theme.TimelineCardPreference
import com.personalproject.mydiary.ui.theme.displayLargeFontFamily
import java.time.format.TextStyle

@Composable
fun DiaryHeader(entry: DiaryEntry, theme: TimelineCardPreference, onDismiss: () -> Unit) {
    val month = DateUtil.getMonthString(entry.timestamp)
    val date = DateUtil.getDayOfMonth(entry.timestamp)
    val specificTime = DateUtil.dayOfWeek(entry.timestamp, style = TextStyle.FULL)
        .dropLast(1)
        .plus(", ")
        .plus(DateUtil.getTime(entry.timestamp))

    Box(modifier = Modifier.fillMaxWidth().offset()) {
        Column(
            modifier = Modifier
                .background(color = theme.primaryTextColor)
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = month,
                color = theme.surfaceColor
            )

            Spacer(Modifier.padding(1.dp))

            Text(
                text = date,
                color = theme.surfaceColor,
                fontFamily = displayLargeFontFamily,
                fontWeight = FontWeight.ExtraLight,
                fontSize = 64.sp
            )

            Text(
                text = specificTime,
                fontSize = 9.sp,
                color = theme.surfaceColor
            )
            Spacer(Modifier.padding(4.dp))

            val weather = if (entry.weather == Weather.UNKNOWN) {
                stringResource(R.string.ui_folder_window_weather_unknown)
            } else {
                entry.weather.label
            }
            val temperatureDisplay = "${entry.temperatureHigh}°/  ${entry.temperatureLow}°"

            Row {
                Icon(
                    painter = painterResource(id = entry.weather.iconRes),
                    contentDescription = weather,
                    modifier = Modifier.size(14.dp),
                    tint = theme.surfaceColor
                )

                Spacer(Modifier.padding(2.dp))

                Text(
                    text = weather,
                    color = theme.surfaceColor,
                    fontFamily = displayLargeFontFamily,
                    fontSize = 8.sp,
                    modifier = Modifier.align(Alignment.CenterVertically).offset(y = (-5).dp)
                )

                Spacer(Modifier.padding(4.dp))

                Text(
                    text = temperatureDisplay,
                    color = theme.surfaceColor,
                    fontFamily = displayLargeFontFamily,
                    fontSize = 8.sp,
                    modifier = Modifier.align(Alignment.CenterVertically).offset(y = (-5).dp)
                )

                Spacer(Modifier.padding(8.dp))

                val unknownLocation = stringResource(R.string.ui_folder_window_location_unknown)
                val isUnknownLocation = entry.location.isNullOrBlank() || entry.location == "UNKNOWN"
                val location = if (isUnknownLocation) unknownLocation else entry.location
                val iconId = if (isUnknownLocation) {
                    R.drawable.ic_ui_wrong_location_24dp
                } else {
                    R.drawable.ic_ui_location_24dp
                }
                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription = location,
                    modifier = Modifier.size(12.dp),
                    tint = theme.surfaceColor
                )

                Spacer(Modifier.padding(2.dp))

                Text(
                    text = location,
                    color = theme.surfaceColor,
                    fontFamily = displayLargeFontFamily,
                    fontSize = 8.sp,
                    modifier = Modifier.align(Alignment.CenterVertically).offset(y = (-5).dp)
                )
            }
        }

        IconButton(
            onClick = { onDismiss() },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_ui_close),
                modifier = Modifier.size(36.dp),
                contentDescription = "exit",
                tint = theme.surfaceColor
            )
        }
    }
}
