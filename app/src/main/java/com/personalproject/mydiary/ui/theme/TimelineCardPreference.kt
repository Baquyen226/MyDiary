package com.personalproject.mydiary.ui.theme

import androidx.compose.ui.graphics.Color

data class TimelineCardPreference(
    val primaryTextColor: Color,
    val surfaceColor: Color,
    val secondaryTextColor: Color,
    val secondarySurfaceColor: Color,
    val iconColor: Color
)

data class UserPreference(
    val timelineCardPreference: TimelineCardPreference
) {
    companion object {
        val Default = BlueSky
    }
}

val BlueSky: UserPreference = UserPreference(
    TimelineCardPreference(
        primaryTextColor = LighterBlue,
        surfaceColor = Color.White,
        secondaryTextColor = Color.Black,
        iconColor = LighterBlue,
        secondarySurfaceColor = LighterBlue
    )
)
