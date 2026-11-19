package com.personalproject.mydiary.ui.feature.common_component.editor

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.RichTextState
import com.personalproject.mydiary.R
import com.personalproject.mydiary.ui.theme.TimelineCardPreference

/**
 * Bottom formatting toolbar offering basic rich-text manipulation:
 * bold, italic, underline, strikethrough, paragraph alignment, and lists.
 */
@Composable
fun EditorFormattingBar(
    state: RichTextState,
    theme: TimelineCardPreference,
    visible: Boolean,
    modifier: Modifier = Modifier,
    onToggleSpanStyle: ((SpanStyle) -> Unit)? = null,
    onAddParagraphStyle: ((ParagraphStyle) -> Unit)? = null,
    onToggleUnorderedList: (() -> Unit)? = null,
    onToggleOrderedList: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            color = theme.surfaceColor,
            tonalElevation = 3.dp,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bold
                item {
                    FormatToggleButton(
                        iconRes = R.drawable.ic_editor_format_bold,
                        contentDescription = "Bold",
                        isSelected = state.currentSpanStyle.fontWeight == FontWeight.Bold,
                        tintColor = theme.primaryTextColor,
                        onClick = {
                            val style = SpanStyle(fontWeight = FontWeight.Bold)
                            onToggleSpanStyle?.invoke(style) ?: state.toggleSpanStyle(style)
                        }
                    )
                }

                // Italic
                item {
                    FormatToggleButton(
                        iconRes = R.drawable.ic_editor_format_italic,
                        contentDescription = "Italic",
                        isSelected = state.currentSpanStyle.fontStyle == FontStyle.Italic,
                        tintColor = theme.primaryTextColor,
                        onClick = {
                            val style = SpanStyle(fontStyle = FontStyle.Italic)
                            onToggleSpanStyle?.invoke(style) ?: state.toggleSpanStyle(style)
                        }
                    )
                }

                // Underline
                item {
                    FormatToggleButton(
                        iconRes = R.drawable.ic_editor_format_underlined,
                        contentDescription = "Underline",
                        isSelected = state.currentSpanStyle.textDecoration?.contains(TextDecoration.Underline) == true,
                        tintColor = theme.primaryTextColor,
                        onClick = {
                            val style = SpanStyle(textDecoration = TextDecoration.Underline)
                            onToggleSpanStyle?.invoke(style) ?: state.toggleSpanStyle(style)
                        }
                    )
                }

                // Strikethrough
                item {
                    FormatToggleButton(
                        iconRes = R.drawable.ic_editor_format_strikethrough,
                        contentDescription = "Strikethrough",
                        isSelected = state.currentSpanStyle.textDecoration?.contains(TextDecoration.LineThrough) == true,
                        tintColor = theme.primaryTextColor,
                        onClick = {
                            val style = SpanStyle(textDecoration = TextDecoration.LineThrough)
                            onToggleSpanStyle?.invoke(style) ?: state.toggleSpanStyle(style)
                        }
                    )
                }

                // Align Left
                item {
                    FormatToggleButton(
                        iconRes = R.drawable.ic_editor_format_align_left,
                        contentDescription = "Align Left",
                        isSelected = state.currentParagraphStyle.textAlign == TextAlign.Start ||
                                state.currentParagraphStyle.textAlign == TextAlign.Unspecified,
                        tintColor = theme.primaryTextColor,
                        onClick = {
                            val style = ParagraphStyle(textAlign = TextAlign.Start)
                            onAddParagraphStyle?.invoke(style) ?: state.addParagraphStyle(style)
                        }
                    )
                }

                // Align Center
                item {
                    FormatToggleButton(
                        iconRes = R.drawable.ic_editor_format_align_center,
                        contentDescription = "Align Center",
                        isSelected = state.currentParagraphStyle.textAlign == TextAlign.Center,
                        tintColor = theme.primaryTextColor,
                        onClick = {
                            val style = ParagraphStyle(textAlign = TextAlign.Center)
                            onAddParagraphStyle?.invoke(style) ?: state.addParagraphStyle(style)
                        }
                    )
                }

                // Align Right
                item {
                    FormatToggleButton(
                        iconRes = R.drawable.ic_editor_format_align_right,
                        contentDescription = "Align Right",
                        isSelected = state.currentParagraphStyle.textAlign == TextAlign.End,
                        tintColor = theme.primaryTextColor,
                        onClick = {
                            val style = ParagraphStyle(textAlign = TextAlign.End)
                            onAddParagraphStyle?.invoke(style) ?: state.addParagraphStyle(style)
                        }
                    )
                }

                // Align Justify
                item {
                    FormatToggleButton(
                        iconRes = R.drawable.ic_editor_format_align_justify,
                        contentDescription = "Align Justify",
                        isSelected = state.currentParagraphStyle.textAlign == TextAlign.Justify,
                        tintColor = theme.primaryTextColor,
                        onClick = {
                            val style = ParagraphStyle(textAlign = TextAlign.Justify)
                            onAddParagraphStyle?.invoke(style) ?: state.addParagraphStyle(style)
                        }
                    )
                }

                // Bullet List
                item {
                    FormatToggleButton(
                        iconRes = R.drawable.ic_editor_format_list_bulleted,
                        contentDescription = "Bullet List",
                        isSelected = state.isUnorderedList,
                        tintColor = theme.primaryTextColor,
                        onClick = {
                            onToggleUnorderedList?.invoke() ?: state.toggleUnorderedList()
                        }
                    )
                }

                // Numbered List
                item {
                    FormatToggleButton(
                        iconRes = R.drawable.ic_editor_format_list_numbered,
                        contentDescription = "Numbered List",
                        isSelected = state.isOrderedList,
                        tintColor = theme.primaryTextColor,
                        onClick = {
                            onToggleOrderedList?.invoke() ?: state.toggleOrderedList()
                        }
                    )
                }

                // Optional Close Button
                if (onClose != null) {
                    item {
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_ui_close),
                                contentDescription = "Close Formatting Bar",
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormatToggleButton(
    @DrawableRes iconRes: Int,
    contentDescription: String,
    isSelected: Boolean,
    tintColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = if (isSelected) tintColor.copy(alpha = 0.2f) else Color.Transparent,
            contentColor = if (isSelected) tintColor else Color.Gray
        ),
        modifier = modifier.size(40.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(20.dp)
        )
    }
}
