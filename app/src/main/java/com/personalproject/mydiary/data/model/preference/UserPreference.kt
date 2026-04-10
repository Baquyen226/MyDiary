package com.personalproject.mydiary.data.model.preference

import androidx.compose.ui.graphics.Color
import com.personalproject.mydiary.ui.theme.LighterBlue
import com.personalproject.mydiary.ui.theme.SurfaceWhite

class UserPreference (
    val timelineCardPreference : TimelineCardPreference
) {
    companion object {
        val Default = BlueSky
    }
}

val BlueSky : UserPreference = UserPreference(
    TimelineCardPreference(
        primaryTextColor = LighterBlue,
        surfaceColor = Color.White,
        secondaryTextColor = Color.Black,
        iconColor = LighterBlue,
        secondarySurfaceColor = LighterBlue
    )
)