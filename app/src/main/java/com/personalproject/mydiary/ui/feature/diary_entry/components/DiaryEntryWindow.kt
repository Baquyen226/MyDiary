package com.personalproject.mydiary.ui.feature.diary_entry.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.personalproject.mydiary.R
import com.personalproject.mydiary.data.mock.MockDiary
import com.personalproject.mydiary.domain.timeline.model.DiaryEntry
import com.personalproject.mydiary.ui.theme.displayLargeFontFamily
import com.personalproject.mydiary.core.util.DateUtil
import com.personalproject.mydiary.ui.theme.BlueSky
import com.personalproject.mydiary.ui.feature.diary_entry.iconRes
import com.personalproject.mydiary.ui.theme.TimelineCardPreference
import java.time.format.TextStyle

/**
 * A modal dialog window that displays a full diary entry.
 *
 * It features a two-tone layout with a colored header for date information and a
 * scrollable white body for the title and content.
 *
 * @param entry The [DiaryEntry] data object containing the timestamp, title, and content.
 * @param theme The color and style preferences to apply to the window components.
 * @param onDismiss Callback lambda to be invoked when the user attempts to close the dialog.
 */
@Preview(showBackground = true, backgroundColor = -14330178)
@Composable
fun DiaryEntryWindow(
   entry : DiaryEntry = MockDiary.mockDiaryEntry_1,
   theme : TimelineCardPreference = BlueSky.timelineCardPreference,
   onDismiss: () -> Unit = {}
) {
   Dialog(
      onDismissRequest = onDismiss,
      properties = DialogProperties(
         usePlatformDefaultWidth = false,
         dismissOnClickOutside = true
      )
   ) {
      Surface(
         modifier = Modifier
            .fillMaxSize(0.9f)
            .wrapContentHeight(),
         shape = RoundedCornerShape(24.dp),
         color = theme.surfaceColor
      ) {
         Column(
            modifier = Modifier
               .background(color = theme.primaryTextColor) // I am sorry.
               .fillMaxWidth()
               .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
         ) {
            Spacer(modifier = Modifier.padding(2.dp))
            DiaryHeader(entry, theme, onDismiss)
            Spacer(modifier = Modifier.padding(4.dp))
            DiaryBody(entry, theme)
            DiaryFooter(entry, theme)
         }
      }
   }
}

@Composable
fun DiaryHeader(entry: DiaryEntry, theme: TimelineCardPreference, onDismiss: () -> Unit) {
   val month = DateUtil.getMonthString(entry.timestamp)
   val date = DateUtil.getDayOfMonth(entry.timestamp)
   val specificTime = DateUtil.dayOfWeek(entry.timestamp, style = TextStyle.FULL)
      .dropLast(1)
      .plus( ", ")
      .plus(DateUtil.getTime(entry.timestamp))

   Box(modifier = Modifier.fillMaxWidth().offset()) {
      Column(
         modifier = Modifier
            .background(color = theme.primaryTextColor) // I am sorry.
            .fillMaxWidth()
            .padding(top = 4.dp),
         horizontalAlignment = Alignment.CenterHorizontally
      ) {
         Text(
            text = month,
            color = theme.surfaceColor // I am sorry.
         )

         Spacer(Modifier.padding(1.dp))

         Text(
            text = date,
            color = theme.surfaceColor,
            fontFamily = displayLargeFontFamily,
            fontWeight = FontWeight.ExtraLight,
            fontSize = 60.sp
         )

         Text(
            text = specificTime,
            fontSize = 9.sp,
            color = theme.surfaceColor
         )
         Spacer(Modifier.padding(4.dp))

         val weather = entry.weather.label
         val temperatureDisplay = "${entry.temperatureHigh}°/  ${entry.temperatureLow}°"

         Row() {
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

            val location = entry.location ?: "UNKNOWN"
            val iconId = if (location == "UNKNOWN") {
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

@Composable
fun DiaryBody(entry: DiaryEntry, theme: TimelineCardPreference) {
   Box(
      modifier = Modifier
         .background(color = theme.surfaceColor)
         .fillMaxWidth()
         .height(400.dp)
   ) {
      val scrollState = rememberScrollState()
      Column(
         modifier = Modifier
            .fillMaxWidth()
            .background(theme.surfaceColor)
            .verticalScroll(scrollState)
            .diaryScrollbar(scrollState, theme.primaryTextColor)
            .padding(6.dp)
      ) {
         Spacer(modifier = Modifier.padding(2.dp))

         Text(
            text = entry.title,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = theme.primaryTextColor,
            fontFamily = displayLargeFontFamily,
            lineHeight = 28.sp
         )

         Spacer(modifier = Modifier.padding(4.dp))

          entry.content?.let {
              Text(
                  text = it,
                  fontSize = 18.sp,
                  lineHeight = 24.sp,
                  modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                  color = theme.secondaryTextColor,
                  style = MaterialTheme.typography.bodyLarge,
                  fontFamily = displayLargeFontFamily
              )
          }
      }
   }
}

@Composable
fun Modifier.diaryScrollbar(
   state: ScrollState,
   color: Color
): Modifier = drawWithContent {
   drawContent()

   val viewportHeight = size.height
   val contentHeight = state.maxValue + viewportHeight

   val rawThumbHeight = (viewportHeight / contentHeight) * viewportHeight
   val minThumbHeight = 48f
   val maxThumbHeight = viewportHeight * 0.4f
   val thumbHeight = rawThumbHeight.coerceIn(minThumbHeight, maxThumbHeight)

   val maxScroll = state.maxValue.toFloat()
   val scrollFraction = if (maxScroll > 0) state.value / maxScroll else 0f
   val verticalPadding = 16f
   val availableHeight = viewportHeight - thumbHeight - (verticalPadding * 2)

   val thumbOffset = verticalPadding + (scrollFraction * availableHeight)

   drawRoundRect(
      color = color,
      topLeft = Offset(x = size.width - 40.0f, y = thumbOffset),
      size = Size(width = 16f, height = thumbHeight),
      cornerRadius = CornerRadius(10f, 10f)
   )
}

@Composable
fun DiaryFooter(entry: DiaryEntry, theme: TimelineCardPreference) {
   Column() {
      Spacer(Modifier.padding(24.dp))
   }
}