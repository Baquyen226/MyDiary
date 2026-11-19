package com.personalproject.mydiary.ui.feature.diary_entry.components.diary_window

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

@Composable
fun Modifier.diaryScrollbar(
    state: ScrollState,
    color: Color
): Modifier = drawWithContent {
    drawContent()

    val viewportHeight = size.height
    val contentHeight = state.maxValue + viewportHeight

    if (state.maxValue <= 0 || state.maxValue == Int.MAX_VALUE) return@drawWithContent

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
        size = Size(width = 18f, height = thumbHeight),
        cornerRadius = CornerRadius(10f, 10f)
    )
}