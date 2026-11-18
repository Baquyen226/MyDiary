package com.personalproject.mydiary.domain.timeline.model

import com.personalproject.mydiary.core.util.toPlainText
import java.time.Instant

/**
 * A concrete timeline item representing a personal journal or diary log.
 * Aligned with the database [EntryEntity].
 *
 * @property id Unique identifier for the diary entry.
 * @property folderId Folder ID of parent folder.
 * @property timestamp The instant the entry represents.
 * @property title Headline or title of the entry.
 * @property location Optional location name where the entry was recorded.
 * @property description A brief summary or subtitle of the entry.
 * @property body The rich or formatted content (e.g. HTML with inline images).
 * @property bodyPlain Clean plain-text representation (without tags) for previews and search.
 * @property bodyFormat Format of the body ("HTML" or "PLAIN").
 * @property colorHex Main color of the diary.
 * @property moodScore User rating of their day (e.g., 1 to 5).
 * @property isBookmarked Whether the entry is starred or pinned.
 * @property isFavorite Whether the entry is marked as favorite.
 * @property weather The recorded weather conditions for this day.
 * @property temperatureHigh Highest temperature of the day.
 * @property temperatureLow Lowest temperature of the day.
 * @property imageUris List of attached image local URIs.
 * @property voiceNoteUri Optional URI of an attached voice recording.
 */
data class DiaryEntry(
    override val id: String,
    override val folderId: String?,
    override val timestamp: Instant?,
    override val title: String,
    override val location: String? = null,
    val description: String? = null,
    val body: String = "",
    val bodyPlain: String = "",
    val bodyFormat: String = "HTML",
    val colorHex: String? = null,
    val moodScore: Int = 0,
    val isBookmarked: Boolean = false,
    val isFavorite: Boolean = false,
    val weather: Weather = Weather.UNKNOWN,
    val temperatureHigh: Int = 0,
    val temperatureLow: Int = 0,
    val imageUris: List<String> = emptyList(),
    val voiceNoteUri: String? = null,
    override val itemType: String = "DIARY"
) : TimelineItem {

    /**
     * old content -> html : )
     */
    constructor(
        id: String,
        folderId: String?,
        timestamp: Instant?,
        title: String,
        location: String? = null,
        description: String? = null,
        content: String?,
        colorHex: String? = null,
        moodScore: Int = 0,
        isBookmarked: Boolean = false,
        isFavorite: Boolean = false,
        weather: Weather = Weather.UNKNOWN,
        temperatureHigh: Int = 0,
        temperatureLow: Int = 0,
        imageUris: List<String> = emptyList(),
        voiceNoteUri: String? = null,
        itemType: String = "DIARY"
    ) : this(
        id = id,
        folderId = folderId,
        timestamp = timestamp,
        title = title,
        location = location,
        description = description,
        body = content ?: "",
        bodyPlain = content?.toPlainText() ?: "",
        bodyFormat = if (content?.contains("<") == true && content.contains(">")) "HTML" else "PLAIN",
        colorHex = colorHex,
        moodScore = moodScore,
        isBookmarked = isBookmarked,
        isFavorite = isFavorite,
        weather = weather,
        temperatureHigh = temperatureHigh,
        temperatureLow = temperatureLow,
        imageUris = imageUris,
        voiceNoteUri = voiceNoteUri,
        itemType = itemType
    )

    val content: String
        get() = body.ifEmpty { bodyPlain }
}
