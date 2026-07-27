package com.personalproject.mydiary.data.schema.timelineItem

import com.personalproject.mydiary.R
import com.personalproject.mydiary.domain.model.timelineItem.TimelineItem

import java.time.Instant

/**
 * A specific property in [com.personalproject.mydiary.domain.model.timelineItem.TimelineItem] uses the following class to describe
 * how the user feels about their day.
 */
enum class Mood {
    ABYSMAL, SAD, ANGRY, ANNOYED, NEUTRAL, MILD, FOND, HAPPY, SUPERCALIFRAGILISTICEXPIALIDOCIOUS
}

/**
 * A specific property in [com.personalproject.mydiary.domain.model.timelineItem.TimelineItem] uses the following class to describe
 * the weather of a day.
 */
enum class Weather(val label: String, val iconRes: Int) {
    SUNNY("SUNNY", R.drawable.ic_weather_sunny_24dp),
    CLOUDY("CLOUDY", R.drawable.ic_weather_cloud_24dp),
    RAINY("RAINY", R.drawable.ic_weather_rainy_24dp),
    SNOWY("SNOWY", R.drawable.ic_weather_snow_24dp),
    STORM("STORM", R.drawable.ic_weather_rainy_heavy_24dp),
    UNKNOWN("Unknown", R.drawable.ic_weather_unknown);

    companion object {
        /**
         * Convert a string to a [Weather] instance.
         */
        fun fromString(value: String?): Weather {
            return entries.find { it.label.equals(value, ignoreCase = true) } ?: UNKNOWN
        }
    }
}

/**
 * A concrete implementation of [com.personalproject.mydiary.domain.model.timelineItem.TimelineItem] representing a personal journal or diary log.
 *
 * This class captures detailed textual content and allows for marking entries as favorites
 * via the [isBookmarked] flag. A diary entry should have two ways of showing it, either through
 * a card for brief summary, or the entire window.
 *
 *
 * @property description A brief summary or subtitle of the entry.
 * @property content The main body text of the diary entry.
 * @property mood How the user feels about their day.
 * @property isBookmarked True if the entry has been starred or pinned by the user.
 * @property weather The weather of this place during the day.
 * @property temperatureHigh Highest temperature of the day
 * @property temperatureLow Highest temperature of the day
 */
class DiaryEntry (
    id: String,
    timestamp: Instant?,
    title: String,
    location: String?,
    tags: List<String>,
    var description: String?,
    var content: String?,
    var mood: Mood = Mood.NEUTRAL,
    var isBookmarked: Boolean,
    var weather: Weather,
    var temperatureHigh: Int,
    var temperatureLow: Int
) : TimelineItem(id, timestamp, title, location, tags, itemType = "DIARY")