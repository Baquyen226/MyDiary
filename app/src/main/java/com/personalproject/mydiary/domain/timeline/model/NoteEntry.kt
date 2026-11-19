package com.personalproject.mydiary.domain.timeline.model

import com.personalproject.mydiary.core.util.toPlainText
import java.time.Instant

/**
 * Represents a note, rule list, memo, or reminder item within the timeline.
 *
 * Unlike a [DiaryEntry], a [NoteEntry] is freeform and does not require weather,
 * mood, or temperature. It can optionally be pinned to the top or scheduled
 * for local push notifications.
 *
 * @property id Unique identifier for the note.
 * @property folderId Folder ID of the parent folder.
 * @property timestamp Creation or targeted occurrence instant.
 * @property title Title or headline of the note.
 * @property location Optional location reference (defaults to null).
 * @property description Optional brief subtitle or summary.
 * @property body The rich or formatted content (HTML with inline images/dividers).
 * @property bodyPlain Clean plain-text representation (without HTML tags).
 * @property bodyFormat Format of the body ("HTML" or "PLAIN").
 * @property colorHex Optional custom accent/card color.
 * @property isPinned Whether this note is pinned to the top of the folder list.
 * @property isBookmarked Whether this note is bookmarked/starred.
 * @property reminderTime Optional future instant when a push notification should trigger.
 * @property isReminderActive Whether the notification reminder is currently armed.
 * @property imageUris Attached local image URIs.
 * @property createdAt Timestamp when this note was originally created.
 * @property updatedAt Timestamp when this note was last edited.
 * @property itemType Category descriptor (defaults to "NOTE").
 */
data class NoteEntry(
    override val id: String,
    override val folderId: String?,
    override val timestamp: Instant?,
    override val title: String,
    override val location: String? = null,
    val description: String? = null,
    val body: String = "",
    val bodyPlain: String = "",
    val bodyFormat: String = "HTML", //fuck this shit im not doing Markdown.
    val colorHex: String? = null,
    val isPinned: Boolean = false,
    val isBookmarked: Boolean = false,
    val reminderTime: Instant? = null,
    val isReminderActive: Boolean = false,
    val imageUris: List<String> = emptyList(),
    val createdAt: Instant = timestamp ?: Instant.now(),
    val updatedAt: Instant = Instant.now(),
    override val itemType: String = "NOTE"
) : TimelineItem {

    /**
     * Convenience secondary constructor that accepts raw content and automatically
     * derives [bodyPlain] and [bodyFormat], matching the editor output.
     */
    constructor(
        id: String,
        folderId: String?,
        timestamp: Instant?,
        title: String,
        description: String? = null,
        content: String?,
        colorHex: String? = null,
        isPinned: Boolean = false,
        isBookmarked: Boolean = false,
        reminderTime: Instant? = null,
        isReminderActive: Boolean = false,
        imageUris: List<String> = emptyList()
    ) : this(
        id = id,
        folderId = folderId,
        timestamp = timestamp,
        title = title,
        location = null,
        description = description,
        body = content ?: "",
        bodyPlain = content?.toPlainText() ?: "",
        bodyFormat = if (content?.contains("<") == true && content.contains(">")) "HTML" else "PLAIN",
        colorHex = colorHex,
        isPinned = isPinned,
        isBookmarked = isBookmarked,
        reminderTime = reminderTime,
        isReminderActive = isReminderActive,
        imageUris = imageUris
    )

    /**
     * Returns the primary text content, falling back to plain text if HTML body is empty.
     */
    val content: String
        get() = body.ifEmpty { bodyPlain }
}
