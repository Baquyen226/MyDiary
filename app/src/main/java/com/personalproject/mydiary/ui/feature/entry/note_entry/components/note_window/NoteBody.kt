package com.personalproject.mydiary.ui.feature.entry.note_entry.components.note_window

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.personalproject.mydiary.core.util.parseDiaryDocument
import com.personalproject.mydiary.data.mock.MockEntry
import com.personalproject.mydiary.domain.timeline.model.NoteEntry
import com.personalproject.mydiary.ui.feature.common_component.diaryScrollbar
import com.personalproject.mydiary.ui.feature.common_component.editor.DiaryDocumentView
import com.personalproject.mydiary.ui.theme.BlueSky
import com.personalproject.mydiary.ui.theme.TimelineCardPreference

/**
 * Body content for [NoteWindow].
 *
 * Renders the note's document view directly without repeating the title.
 *
 * @param noteEntry The [NoteEntry] whose body/image contents will be parsed and displayed.
 * @param theme The styling preferences to apply.
 */
@Composable
@Preview
fun NoteBody(
    noteEntry: NoteEntry = MockEntry.mockNoteEntry_1,
    theme: TimelineCardPreference = BlueSky.timelineCardPreference
) {
    Box(
        modifier = Modifier
            .background(color = theme.surfaceColor)
            .fillMaxWidth()
            .fillMaxSize(0.82f)
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(theme.surfaceColor)
                .diaryScrollbar(scrollState, theme.primaryTextColor)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 2.dp)
        ) {
            val rawBody = noteEntry.body.ifEmpty { noteEntry.bodyPlain }
            val blocks = remember(rawBody, noteEntry.imageUris) {
                parseDiaryDocument(rawBody, noteEntry.imageUris)
            }

            // Common document view shared across diary entries and notes
            DiaryDocumentView(
                blocks = blocks,
                theme = theme
            )

            // Bottom scroll margin
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}
