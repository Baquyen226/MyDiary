package com.personalproject.mydiary.ui.feature.diary_entry

import androidx.annotation.DrawableRes
import com.personalproject.mydiary.R
import com.personalproject.mydiary.domain.timeline.model.Weather

@get:DrawableRes
val Weather.iconRes: Int
    get() = when (this) {
        Weather.SUNNY -> R.drawable.ic_weather_sunny_24dp
        Weather.CLOUDY -> R.drawable.ic_weather_cloud_24dp
        Weather.RAINY -> R.drawable.ic_weather_rainy_24dp
        Weather.SNOWY -> R.drawable.ic_weather_snow_24dp
        Weather.STORM -> R.drawable.ic_weather_rainy_heavy_24dp
        Weather.UNKNOWN -> R.drawable.ic_weather_unknown
    }

