package com.personalproject.mydiary.domain.timeline.model

import java.time.Instant

/**
 * A concrete timeline item representing a personal journal or diary log.
 * I am not sure why I decided to make an abstract [TimelineItem]. Please
 * do not kill me.
 *
 * Oh? OOP concept? Inheritance? Much cool? IT'S NOT.
 *
 * @property id Unique identifier for the diary entry.
 * @property folderId Folder ID of parent.
 * @property timestamp The instant the entry represents.
 * @property title Headline or title of the entry.
 * @property location Optional location where the entry was recorded.
 * @property description A brief summary or subtitle of the entry.
 * @property content The main body text of the diary entry.
 * @property colorHex Main color of the diary.
 * @property moodScore User rating of their day (e.g., 1 to 5).
 * @property isBookmarked Whether the entry is starred or pinned.
 * @property weather The recorded weather conditions for this day.
 * @property temperatureHigh Highest temperature of the day.
 * @property temperatureLow Lowest temperature of the day.
 */
data class DiaryEntry(
    override val id: String,
    override val folderId: String?,
    override val timestamp: Instant?,
    override val title: String,
    override val location: String? = null,
    val description: String? = null,
    val content: String? = null,
    val colorHex: String? = null,
    val moodScore: Int = 3,
    val isBookmarked: Boolean = false,
    val isFavorite: Boolean = false,
    val weather: Weather = Weather.UNKNOWN,
    val temperatureHigh: Int = 0,
    val temperatureLow: Int = 0,
    override val itemType: String = "DIARY"
) : TimelineItem
