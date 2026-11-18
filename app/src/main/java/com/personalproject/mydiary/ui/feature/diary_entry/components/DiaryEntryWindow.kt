package com.personalproject.mydiary.ui.feature.diary_entry.components

import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.personalproject.mydiary.R
import com.personalproject.mydiary.core.util.DateUtil
import com.personalproject.mydiary.core.util.toPlainText
import com.personalproject.mydiary.data.mock.MockDiary
import com.personalproject.mydiary.domain.timeline.model.DiaryEntry
import com.personalproject.mydiary.ui.feature.diary_entry.iconRes
import com.personalproject.mydiary.ui.theme.BlueSky
import com.personalproject.mydiary.ui.theme.TimelineCardPreference
import com.personalproject.mydiary.ui.theme.displayLargeFontFamily
import java.time.format.TextStyle as JavaTextStyle
import androidx.core.net.toUri

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
               .background(color = theme.primaryTextColor)
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
   val specificTime = DateUtil.dayOfWeek(entry.timestamp, style = JavaTextStyle.FULL)
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

         val weather = entry.weather.label
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

private sealed interface ContentSegment {
   data class Text(val text: String) : ContentSegment
   data class Image(val uriString: String) : ContentSegment
}

private fun parseContentToSegments(rawContent: String, fallbackImageUris: List<String>): List<ContentSegment> {
   if (rawContent.isBlank()) {
      return fallbackImageUris.map { ContentSegment.Image(it) }
   }

   val imgRegex = Regex("""<img[^>]+src=["']([^"']+)["'][^>]*>""", RegexOption.IGNORE_CASE)
   val matches = imgRegex.findAll(rawContent).toList()

   if (matches.isEmpty()) {
      val segments = mutableListOf<ContentSegment>()
      val plain = rawContent.toPlainText()
      if (plain.isNotBlank()) {
         segments.add(ContentSegment.Text(plain))
      }
      fallbackImageUris.forEach {
         segments.add(ContentSegment.Image(it))
      }
      return segments
   }

   val segments = mutableListOf<ContentSegment>()
   var lastIndex = 0

   for (match in matches) {
      val textBefore = rawContent.substring(lastIndex, match.range.first).toPlainText()
      if (textBefore.isNotBlank()) {
         segments.add(ContentSegment.Text(textBefore))
      }
      val src = match.groupValues[1]
      if (src.isNotBlank()) {
         segments.add(ContentSegment.Image(src))
      }
      lastIndex = match.range.last + 1
   }

   if (lastIndex < rawContent.length) {
      val textAfter = rawContent.substring(lastIndex).toPlainText()
      if (textAfter.isNotBlank()) {
         segments.add(ContentSegment.Text(textAfter))
      }
   }

   return segments
}

@Composable
fun DiaryBody(entry: DiaryEntry, theme: TimelineCardPreference) {
   Box(
      modifier = Modifier
         .background(color = theme.surfaceColor)
         .fillMaxWidth()
         .fillMaxSize(0.8f)
   ) {
      val scrollState = rememberScrollState()
      Column(
         modifier = Modifier
            .fillMaxWidth()
            .background(theme.surfaceColor)
            .verticalScroll(scrollState)
            .diaryScrollbar(scrollState, theme.primaryTextColor)
            .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
         Spacer(modifier = Modifier.height(4.dp))

         Text(
            text = entry.title,
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            modifier = Modifier
               .fillMaxWidth()
               .align(Alignment.CenterHorizontally),
            color = theme.primaryTextColor,
            fontFamily = displayLargeFontFamily,
            lineHeight = 42.sp
         )

         Spacer(modifier = Modifier.height(12.dp))

         val rawBody = entry.body.ifEmpty { entry.bodyPlain }
         val segments = remember(rawBody, entry.imageUris) {
            parseContentToSegments(rawBody, entry.imageUris)
         }

         // Render content and images in natural order
         // TODO: Depending on the locality, different font size is needed
         segments.forEach { segment ->
            when (segment) {
               is ContentSegment.Text -> {
                  Text(
                     text = segment.text,
                     fontSize = 18.sp,
                     lineHeight = 24.sp,
                     color = theme.secondaryTextColor,
                     style = MaterialTheme.typography.bodyLarge,
                     fontFamily = displayLargeFontFamily,
                     modifier = Modifier.padding(bottom = 12.dp).padding(horizontal = 9.dp)
                  )
               }
               is ContentSegment.Image -> {
                  ReadOnlyWebImage(
                     uriString = segment.uriString,
                     modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .padding(horizontal = 9.dp)
                  )
               }
            }
         }

         // look at some point you gotta stop slapping magic constants everywhere
         Spacer(modifier = Modifier.height(164.dp))
      }
   }
}

@Composable
private fun ReadOnlyWebImage(
   uriString: String,
   modifier: Modifier = Modifier
) {
   val context = LocalContext.current
   val bitmap = remember(uriString) {
      runCatching {
         val uri = uriString.toUri()
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = android.graphics.ImageDecoder.createSource(context.contentResolver, uri)
            android.graphics.ImageDecoder.decodeBitmap(source)
         } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
         }
      }.getOrNull()
   }

   bitmap?.let {
      Image(
         bitmap = it.asImageBitmap(),
         contentDescription = "Entry image",
         contentScale = ContentScale.FillWidth,
         modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 280.dp)
            .clip(RoundedCornerShape(12.dp))
      )
   }
}


@Composable
fun DiaryFooter(
   entry: DiaryEntry = MockDiary.mockDiaryEntry_1,
   theme: TimelineCardPreference = BlueSky.timelineCardPreference,
   onMoreOptionsClick: () -> Unit = {},
   onLocationClick: () -> Unit = {},
   onCameraClick: () -> Unit = {},
   onMusicClick: () -> Unit = {},
   onDeleteClick: () -> Unit = {}
) {
   Column(
      modifier = Modifier
         .fillMaxWidth()
         .background(theme.primaryTextColor)
   ) {
      Row(
         modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
         horizontalArrangement = Arrangement.SpaceBetween,
         verticalAlignment = Alignment.CenterVertically
      ) {
         // More Options
         IconButton(onClick = onMoreOptionsClick) {
            Icon(
               painter = painterResource(R.drawable.ic_ui_more_horizontal_24dp),
               contentDescription = "More Options",
               tint = Color.White
            )
         }

         // Location
         IconButton(onClick = onLocationClick) {
            Icon(
               painter = painterResource(R.drawable.ic_ui_location_24dp),
               contentDescription = "Location",
               tint = Color.White
            )
         }

         // Camera
         IconButton(onClick = onCameraClick) {
            Icon(
               painter = painterResource(R.drawable.ic_ui_photo_camera_24dp),
               contentDescription = "Camera",
               tint = Color.White
            )
         }

         // Music
         IconButton(onClick = onMusicClick) {
            Icon(
               painter = painterResource(R.drawable.ic_ui_music_note_24dp),
               contentDescription = "Music",
               tint = Color.White
            )
         }

         // Delete
         IconButton(onClick = onDeleteClick) {
            Icon(
               painter = painterResource(R.drawable.ic_ui_delete),
               contentDescription = "Delete",
               tint = Color.White
            )
         }
      }
   }
}
