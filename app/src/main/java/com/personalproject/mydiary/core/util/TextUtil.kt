package com.personalproject.mydiary.core.util

import androidx.core.text.HtmlCompat

/**
 * Converts formatted HTML content to clean, unformatted plain text.
 * Preserves newlines and paragraph breaks.
 */
fun String?.toPlainText(): String {
    if (this.isNullOrBlank()) return ""

    // If it's already plain text (no HTML tags), preserve all line breaks as-is!
    if (!this.contains("<") || !this.contains(">")) {
        return this
    }

    return try {
        HtmlCompat.fromHtml(
            this.replace("<br>", "\n").replace("<br/>", "\n").replace("<br />", "\n"),
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )
            .toString()
            .trimEnd()
    } catch (e: Exception) {
        // Fallback for JVM unit tests or if HtmlCompat fails
        this.replace(Regex("<img[^>]*>"), "")
            .replace(Regex("<br\\s*/?>"), "\n")
            .replace(Regex("</p>"), "\n")
            .replace(Regex("<[^>]*>"), "")
            .trimEnd()
    }
}

/**
 * Extracts the first non-blank line of text for diary card previews.
 * Attempts to find the first line of text. If not found (e.g. entry contains only images or voice recordings),
 * returns an empty string to leave the preview blank.
 */
fun String?.toPreviewText(): String {
    if (this.isNullOrBlank()) return ""

    val plain = this.toPlainText()
    return plain.lineSequence()
        .map { it.replace('\uFFFC', ' ').trim() }
        .firstOrNull { it.isNotBlank() }
        ?: ""
}
