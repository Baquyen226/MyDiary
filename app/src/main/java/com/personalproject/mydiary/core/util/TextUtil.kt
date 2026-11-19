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

/**
 * Escapes common HTML special characters in plain text strings.
 */
fun escapeHtml(text: String): String {
    return text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;")
}

/**
 * Checks whether the given string contains actual HTML tags.
 */
fun isHtmlContent(content: String): Boolean {
    val tagRegex = Regex("""</?[a-zA-Z][a-zA-Z0-9]*(\s+[^>]*)?>""")
    return tagRegex.containsMatchIn(content)
}

/**
 * Normalizes content for rich text rendering and editor loading.
 * Preserves all formatting tags and converts intentional text newlines into explicit line breaks (<br>)
 * while properly stripping indentation/source newlines around structural HTML tags (<ul>, <ol>, <li>, <p>)
 * so that no unwanted empty lines appear before or after lists and blocks.
 */
fun normalizeContentForHtml(raw: String): String {
    val trimmed = raw.trim()
    if (trimmed.isEmpty()) return ""

    val hasHtmlTags = isHtmlContent(trimmed)
    if (!hasHtmlTags) {
        // Pure plain text: escape special chars and convert each newline to <br>
        val escaped = escapeHtml(trimmed)
        return escaped.replace("\r\n", "\n").replace("\r", "\n").replace("\n", "<br>")
    }

    // Replace empty paragraph tags with <br> so empty lines don't collapse
    val withoutEmptyP = trimmed.replace(Regex("""<p>\s*(<br\s*/?>)?\s*</p>""", RegexOption.IGNORE_CASE), "<br>")

    // Normalize Windows CRLF to LF
    val normalized = withoutEmptyP.replace("\r\n", "\n").replace("\r", "\n")
    if (!normalized.contains("\n")) return normalized

    // Handle intentional extra blank lines between block tags (e.g. </p>\n\n<p> has 1 extra blank line -> </p><br><p>)
    val blockBetweenRegex = Regex("""(</?(?:p|h[1-6]|ul|ol|li)[^>]*>)\s*\n(\s*\n)+(\s*</?(?:p|h[1-6]|ul|ol|li)[^>]*>)""", RegexOption.IGNORE_CASE)
    val withBlockBreaks = blockBetweenRegex.replace(normalized) { match ->
        val firstTag = match.groups[1]?.value ?: ""
        val secondTag = match.groups[3]?.value ?: ""
        val newlineCount = match.value.count { it == '\n' }
        val extraBreaks = "<br>".repeat((newlineCount - 1).coerceAtLeast(1))
        "$firstTag$extraBreaks$secondTag"
    }

    // Clean up single formatting newlines between structural block and list tags:
    // e.g. <ul>\n<li>, </li>\n<li>, </li>\n</ul>, </ol>\n<p>, </p>\n<p>
    val tagNewlineRegex = Regex("""(</?(?:p|h[1-6]|ul|ol|li)[^>]*>)\s*\n\s*(</?(?:p|h[1-6]|ul|ol|li)[^>]*>)""", RegexOption.IGNORE_CASE)
    val cleanedBlocks = tagNewlineRegex.replace(withBlockBreaks) { match ->
        "${match.groups[1]?.value ?: ""}${match.groups[2]?.value ?: ""}"
    }

    // Clean up any remaining formatting newlines immediately after opening block/list tags
    val cleanAfterOpen = cleanedBlocks.replace(Regex("""(<(?:ul|ol|li|p|h[1-6])[^>]*>)\s*\n\s*""", RegexOption.IGNORE_CASE)) { match ->
        match.groups[1]?.value ?: ""
    }

    // Clean up any remaining formatting newlines immediately before closing block/list tags
    val cleanBeforeClose = cleanAfterOpen.replace(Regex("""\s*\n\s*(</(?:ul|ol|li|p|h[1-6])>)""", RegexOption.IGNORE_CASE)) { match ->
        match.groups[1]?.value ?: ""
    }

    // Remaining newlines inside body text are intentional user line breaks
    return cleanBeforeClose.replace("\n", "<br>")
}
