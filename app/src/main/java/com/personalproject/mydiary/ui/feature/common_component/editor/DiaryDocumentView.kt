package com.personalproject.mydiary.ui.feature.common_component.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.ui.BasicRichText
import com.personalproject.mydiary.ui.theme.BlueSky
import com.personalproject.mydiary.ui.theme.TimelineCardPreference
import com.personalproject.mydiary.ui.theme.displayLargeFontFamily

/**
 * A common, read-only multi-block document viewer.
 *
 * Renders structured [EditorBlock]s (rich text, media images, dividers) in natural order
 * with consistent styling, paddings, and typography. Shared across Diaries, Notes, and previews.
 *
 * @param blocks The list of [EditorBlock]s to display.
 * @param theme The styling preferences to apply for colors.
 * @param modifier The layout modifier for the container column.
 * @param fontSize Font size for rich text blocks.
 * @param lineHeight Line height for rich text blocks.
 * @param textPaddingHorizontal Horizontal padding for text and divider content.
 * @param blockBottomPadding Bottom spacing between blocks.
 */
@OptIn(ExperimentalRichTextApi::class)
@Composable
fun DiaryDocumentView(
    modifier: Modifier = Modifier,
    blocks: List<EditorBlock>,
    theme: TimelineCardPreference = BlueSky.timelineCardPreference,
    fontSize: TextUnit = 18.sp,
    lineHeight: TextUnit = 32.sp,
    textPaddingHorizontal: Dp = 9.dp,
    blockBottomPadding: Dp = 6.dp
) {
    Column(modifier = modifier.fillMaxWidth()) {
        blocks.forEach { block ->
            when (block) {
                is EditorBlock.Text -> {
                    BasicRichText(
                        state = block.state,
                        style = TextStyle(
                            fontFamily = displayLargeFontFamily,
                            fontSize = fontSize,
                            lineHeight = lineHeight,
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.None
                            ),
                            color = theme.secondaryTextColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = blockBottomPadding)
                            .padding(horizontal = textPaddingHorizontal)
                    )
                }

                is EditorBlock.Image -> {
                    ArticleImageItem(
                        uriString = block.uriString,
                                                modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = blockBottomPadding)
                            .padding(horizontal = textPaddingHorizontal)
                    )
                }

                is EditorBlock.Divider -> {
                    EditorDividerItem(
                        theme = theme,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .padding(horizontal = textPaddingHorizontal)
                    )
                }
            }
        }
    }
}
