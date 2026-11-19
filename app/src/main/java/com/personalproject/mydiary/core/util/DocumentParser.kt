package com.personalproject.mydiary.core.util

import com.mohamedrejeb.richeditor.model.RichTextState
import com.personalproject.mydiary.ui.feature.common_component.editor.EditorBlock

private val TAG_REGEX = Regex(
    """(<p>\s*<img[^>]+src=["']([^"']+)["'][^>]*>\s*</p>)|(<img[^>]+src=["']([^"']+)["'][^>]*>)|(<hr\s*/?>)|([─\-_]{3,})""",
    RegexOption.IGNORE_CASE
)

private val SRC_REGEX = Regex("""src=["']([^"']+)["']""", RegexOption.IGNORE_CASE)

/**
 * Parses raw diary content (HTML, plain text, inline images, dividers) into structured [com.personalproject.mydiary.ui.feature.common_component.editor.EditorBlock]s.
 * Serves as the single source of truth for both:
 * - DiaryBody (read-only view in DiaryEntryWindow)
 * - DiaryEditorState (editable multi-block document in DiaryEditor)
 */
fun parseDiaryDocument(
    rawContent: String,
    fallbackImageUris: List<String> = emptyList()
): List<EditorBlock> {
    if (rawContent.isBlank()) {
        return fallbackImageUris.map { EditorBlock.Image(it) }
    }

    val matches = TAG_REGEX.findAll(rawContent).toList()
    val blocks = mutableListOf<EditorBlock>()
    val addedImages = mutableSetOf<String>()

    if (matches.isEmpty()) {
        val html = normalizeContentForHtml(rawContent)
        if (html.isNotBlank()) {
            val textState = RichTextState().setHtml(html)
            blocks.add(EditorBlock.Text(textState))
        }
    } else {
        var lastIndex = 0
        for (match in matches) {
            val rawBefore = rawContent.substring(lastIndex, match.range.first)
            val htmlBefore = normalizeContentForHtml(rawBefore)
            if (htmlBefore.isNotBlank()) {
                val textState = RichTextState().setHtml(htmlBefore)
                blocks.add(EditorBlock.Text(textState))
            }

            val matchValue = match.value
            if (matchValue.contains("<hr", ignoreCase = true) || matchValue.trim().all { it == '─' || it == '-' || it == '_' }) {
                blocks.add(EditorBlock.Divider())
            } else {
                val src = SRC_REGEX.find(matchValue)?.groups?.get(1)?.value ?: ""
                if (src.isNotBlank()) {
                    blocks.add(EditorBlock.Image(src))
                    addedImages.add(src)
                }
            }
            lastIndex = match.range.last + 1
        }

        if (lastIndex < rawContent.length) {
            val rawAfter = rawContent.substring(lastIndex)
            val htmlAfter = normalizeContentForHtml(rawAfter)
            if (htmlAfter.isNotBlank()) {
                val textState = RichTextState().setHtml(htmlAfter)
                blocks.add(EditorBlock.Text(textState))
            }
        }
    }

    // Attach any fallback or trailing images not already embedded in content
    fallbackImageUris.forEach { uri ->
        if (uri !in addedImages) {
            blocks.add(EditorBlock.Image(uri))
            addedImages.add(uri)
        }
    }

    return blocks
}
