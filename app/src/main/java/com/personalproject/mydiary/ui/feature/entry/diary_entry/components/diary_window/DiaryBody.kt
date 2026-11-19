package com.personalproject.mydiary.ui.feature.entry.diary_entry.components.diary_window

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personalproject.mydiary.core.util.parseDiaryDocument
import com.personalproject.mydiary.domain.timeline.model.DiaryEntry
import com.personalproject.mydiary.ui.feature.common_component.diaryScrollbar
import com.personalproject.mydiary.ui.feature.common_component.editor.DiaryDocumentView
import com.personalproject.mydiary.ui.theme.TimelineCardPreference
import com.personalproject.mydiary.ui.theme.displayLargeFontFamily
import kotlin.text.ifEmpty

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
                .diaryScrollbar(scrollState, theme.primaryTextColor)
                .verticalScroll(scrollState)
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
            val blocks = remember(rawBody, entry.imageUris) {
                parseDiaryDocument(rawBody, entry.imageUris)
            }

            // Common document view shared across diary entries and notes
            DiaryDocumentView(
                blocks = blocks,
                theme = theme
            )

            // Margin space at the end of the scrollable content
            Spacer(modifier = Modifier.height(164.dp))
        }
    }
}
