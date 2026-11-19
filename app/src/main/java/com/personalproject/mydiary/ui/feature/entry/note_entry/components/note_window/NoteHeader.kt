package com.personalproject.mydiary.ui.feature.entry.note_entry.components.note_window

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personalproject.mydiary.R
import com.personalproject.mydiary.data.mock.MockEntry
import com.personalproject.mydiary.domain.timeline.model.NoteEntry
import com.personalproject.mydiary.ui.theme.BlueSky
import com.personalproject.mydiary.ui.theme.TimelineCardPreference
import com.personalproject.mydiary.ui.theme.displayLargeFontFamily

/**
 * A compact, two-tone header for [NoteWindow].
 *
 * Significantly shorter than the diary header: displays the note's title on the
 * first line and its description on the bottom line.
 *
 * @param noteEntry The note entry containing title and description.
 * @param theme The color and style preferences to apply.
 * @param onDismiss Callback when the close button is clicked.
 */
@Composable
@Preview
fun NoteHeader(
    noteEntry: NoteEntry = MockEntry.mockNoteEntry_1,
    theme: TimelineCardPreference = BlueSky.timelineCardPreference,
    onDismiss: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = theme.primaryTextColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.size(12.dp))

            Text(
                text = noteEntry.title,
                color = Color.White,
                fontFamily = displayLargeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )

            if (!noteEntry.description.isNullOrBlank()) {

                Text(
                    text = noteEntry.description,
                    color = Color.White.copy(alpha = 0.92f),
                    fontFamily = displayLargeFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            } else { Spacer(modifier = Modifier.height(23.dp)) }
        }

        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_ui_close),
                modifier = Modifier.size(28.dp),
                contentDescription = "Close Note",
                tint = Color.White
            )
        }
    }
}
