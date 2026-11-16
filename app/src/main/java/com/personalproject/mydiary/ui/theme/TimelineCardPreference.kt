package com.personalproject.mydiary.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorInt

data class TimelineCardPreference(
    val primaryTextColor: Color,
    val surfaceColor: Color,
    val secondaryTextColor: Color,
    val subtitleTextColor: Color = LightGrey,
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

/**
 * Converts a hex color string (e.g. "#E57373") into a [TimelineCardPreference].
 *
 * The hex color becomes the primary/accent color; a lightened version (high-value,
 * low-saturation HSV variant) is used as the surface/background color.
 * Falls back to [BlueSky] if [hexColor] is null or unparseable.
 */
fun colorHexToCardPreference(hexColor: String?): TimelineCardPreference {
    val baseColor = hexColor
        ?.runCatching { Color(this.toColorInt()) }
        ?.getOrNull()
        ?: return BlueSky.timelineCardPreference

    // Lighten via HSV: push value to ~0.97, drop saturation to ~0.12
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(baseColor.toArgb(), hsv)
    hsv[1] = hsv[1] * 0.18f   // desaturate significantly
    hsv[2] = 0.97f             // near-white value
    val lightColor = Color(android.graphics.Color.HSVToColor(hsv))

    return TimelineCardPreference(
        primaryTextColor = baseColor,
        surfaceColor = Color.White,
        secondaryTextColor = Color(0xFF333333),
        secondarySurfaceColor = lightColor,
        iconColor = baseColor
    )
}
