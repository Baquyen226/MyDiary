package com.personalproject.mydiary.ui.feature.entry.note_entry.components.note_window

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.personalproject.mydiary.data.mock.MockEntry
import com.personalproject.mydiary.domain.timeline.model.NoteEntry
import com.personalproject.mydiary.ui.theme.BlueSky
import com.personalproject.mydiary.ui.theme.TimelineCardPreference
import com.personalproject.mydiary.ui.theme.colorHexToCardPreference

/**
 * A modal dialog window that displays a full note entry.
 *
 * It features a shorter, two-tone header displaying the title on the first line and
 * the description on the bottom line, a pure document view body, and a three-button
 * footer (More_horizontal, add_alarm, delete).
 *
 * @param modifier Modifier for styling and layout.
 * @param noteEntry The [NoteEntry] data object to display.
 * @param theme The color and style preferences to apply.
 * @param onDismiss Callback when the user closes the dialog.
 * @param onEditClick Callback to edit the note.
 * @param onColorChange Callback when the note's color hex is updated.
 * @param onAddAlarmClick Callback when the add_alarm button is clicked.
 * @param onDeleteClick Callback when the delete button is clicked.
 * @param onPinToggle Callback to toggle the note's pinned state.
 * @param onBookmarkToggle Callback to toggle the note's bookmarked state.
 */
@Composable
fun NoteWindow(
    modifier: Modifier = Modifier,
    noteEntry: NoteEntry = MockEntry.mockNoteEntry_1,
    theme: TimelineCardPreference = BlueSky.timelineCardPreference,
    onDismiss: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onColorChange: (String) -> Unit = {},
    onAddAlarmClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onPinToggle: () -> Unit = {},
    onBookmarkToggle: () -> Unit = {}
) {
    val isDark = isSystemInDarkTheme()
    val effectiveTheme = remember(noteEntry.colorHex, theme, isDark) {
        noteEntry.colorHex?.takeIf { it.isNotBlank() }?.let { colorHexToCardPreference(it, isDark) } ?: theme
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = effectiveTheme.surfaceColor
        ) {
            Column(
                modifier = Modifier
                    .background(color = effectiveTheme.primaryTextColor)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NoteHeader(
                    noteEntry = noteEntry,
                    theme = effectiveTheme,
                    onDismiss = onDismiss
                )
                NoteBody(
                    noteEntry = noteEntry,
                    theme = effectiveTheme
                )
                NoteFooter(
                    noteEntry = noteEntry,
                    theme = effectiveTheme,
                    onEditClick = onEditClick,
                    onColorChange = onColorChange,
                    onAddAlarmClick = onAddAlarmClick,
                    onDeleteClick = onDeleteClick,
                    onPinToggle = onPinToggle,
                    onBookmarkToggle = onBookmarkToggle
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoteWindowPreview() {
    NoteWindow(
        noteEntry = MockEntry.mockNoteEntry_1
    )
}
