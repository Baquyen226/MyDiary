package com.personalproject.mydiary.domain.timeline.model

/**
 * Describes the weather conditions for a diary entry.
 */
enum class Weather(val label: String) {
    SUNNY("SUNNY"),
    CLOUDY("CLOUDY"),
    RAINY("RAINY"),
    SNOWY("SNOWY"),
    STORM("STORM"),
    UNKNOWN("Unknown");

    companion object {
        fun fromString(value: String?): Weather {
            return entries.firstOrNull { it.label.equals(value, ignoreCase = true) } ?: UNKNOWN
        }
    }
}
