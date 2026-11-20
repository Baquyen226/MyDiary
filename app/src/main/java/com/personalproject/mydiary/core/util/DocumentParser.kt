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
 *
 * Ensures list tags (`<ul>`, `<ol>`) split by dividers or images remain properly balanced
 * so that list items on both sides of a divider retain their bullets or numbering.
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

    var openUl = false
    var openOl = false
    var currentOlIndex = 1

    fun balanceChunk(chunk: String): String {
        var result = chunk.trim()
        if (result.isEmpty()) return ""

        val hasUlOpen = result.contains("<ul", ignoreCase = true)
        val hasOlOpen = result.contains("<ol", ignoreCase = true)

        if (openUl && !hasUlOpen) {
            result = "<ul>$result"
        } else if (openOl && !hasOlOpen) {
            result = "<ol start=\"$currentOlIndex\">$result"
        }

        val totalUlOpens = Regex("<ul\\b", RegexOption.IGNORE_CASE).findAll(result).count()
        val totalUlCloses = Regex("</ul>", RegexOption.IGNORE_CASE).findAll(result).count()
        val totalOlOpens = Regex("<ol\\b", RegexOption.IGNORE_CASE).findAll(result).count()
        val totalOlCloses = Regex("</ol>", RegexOption.IGNORE_CASE).findAll(result).count()

        if (totalUlOpens > totalUlCloses) {
            result = "$result</ul>"
            openUl = true
        } else {
            openUl = false
        }

        if (totalOlOpens > totalOlCloses) {
            result = "$result</ol>"
            openOl = true
            val liCount = Regex("<li\\b", RegexOption.IGNORE_CASE).findAll(result).count().coerceAtLeast(1)
            val startMatch = Regex("""<ol\b[^>]*\bstart=["']?(\d+)""", RegexOption.IGNORE_CASE).find(result)
            val chunkStart = startMatch?.groupValues?.get(1)?.toIntOrNull() ?: currentOlIndex
            currentOlIndex = chunkStart + liCount
        } else {
            openOl = false
            currentOlIndex = 1
        }

        return result
    }

    if (matches.isEmpty()) {
        val balanced = balanceChunk(rawContent)
        val html = normalizeContentForHtml(balanced)
        if (html.isNotBlank()) {
            val textState = RichTextState().setHtml(html)
            blocks.add(EditorBlock.Text(textState))
        }
    } else {
        var lastIndex = 0
        for (match in matches) {
            val rawBefore = rawContent.substring(lastIndex, match.range.first)
            val balancedBefore = balanceChunk(rawBefore)
            val htmlBefore = normalizeContentForHtml(balancedBefore)
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
            val balancedAfter = balanceChunk(rawAfter)
            val htmlAfter = normalizeContentForHtml(balancedAfter)
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
