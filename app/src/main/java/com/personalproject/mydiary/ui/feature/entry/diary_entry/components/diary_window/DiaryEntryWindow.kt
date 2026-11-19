package com.personalproject.mydiary.ui.feature.entry.diary_entry.components.diary_window

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.personalproject.mydiary.data.mock.MockEntry
import com.personalproject.mydiary.domain.timeline.model.DiaryEntry
import com.personalproject.mydiary.ui.theme.BlueSky
import com.personalproject.mydiary.ui.theme.TimelineCardPreference
import com.personalproject.mydiary.ui.theme.colorHexToCardPreference

/**
 * A modal dialog window that displays a full diary entry.
 *
 * It features a two-tone layout with a colored header for date information and a
 * scrollable white body for the title and content.
 *
 * @param entry The [DiaryEntry] data object containing the timestamp, title, and content.
 * @param theme The color and style preferences to apply to the window components.
 * @param onDismiss Callback lambda to be invoked when the user attempts to close the dialog.
 * @param onEditClick Callback to enter the DiaryEditor with this entry.
 * @param onColorChange Callback when the diary's color hex is updated.
 * @param onMoodChange Callback when the diary's mood score is updated.
 */
@Composable
fun DiaryEntryWindow(
   entry : DiaryEntry = MockEntry.mockDiaryEntry_1,
   theme : TimelineCardPreference = BlueSky.timelineCardPreference,
   onDismiss: () -> Unit = {},
   onEditClick: () -> Unit = {},
   onColorChange: (String) -> Unit = {},
   onMoodChange: (Int) -> Unit = {}
) {
   val isDark = isSystemInDarkTheme()
   val effectiveTheme = remember(entry.colorHex, theme, isDark) {
      entry.colorHex?.takeIf { it.isNotBlank() }?.let { colorHexToCardPreference(it, isDark) } ?: theme
   }

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
         color = effectiveTheme.surfaceColor
      ) {
         Column(
            modifier = Modifier
               .background(color = effectiveTheme.primaryTextColor)
               .fillMaxWidth()
               .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
         ) {
            Spacer(modifier = Modifier.padding(2.dp))
            DiaryHeader(entry, effectiveTheme, onDismiss)
            Spacer(modifier = Modifier.padding(4.dp))
            DiaryBody(entry, effectiveTheme)
            DiaryFooter(
               entry = entry,
               theme = effectiveTheme,
               onEditClick = onEditClick,
               onColorChange = onColorChange,
               onMoodChange = onMoodChange
            )
         }
      }
   }
}




