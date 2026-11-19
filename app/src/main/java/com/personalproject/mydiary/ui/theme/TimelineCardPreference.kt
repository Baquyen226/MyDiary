package com.personalproject.mydiary.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

val BlueSkyLight = TimelineCardPreference(
    primaryTextColor = LighterBlue,
    surfaceColor = Color.White,
    secondaryTextColor = Color(0xFF333333),
    subtitleTextColor = LightGrey,
    iconColor = LighterBlue,
    secondarySurfaceColor = Color(0xFFEBF5FA)
)

val BlueSkyDark = TimelineCardPreference(
    primaryTextColor = Color(0xFF64B5F6),
    surfaceColor = Color(0xFF1E1E1E),
    secondaryTextColor = Color(0xFFE6E6E6),
    subtitleTextColor = Color(0xFFA5A5A5),
    iconColor = Color(0xFF64B5F6),
    secondarySurfaceColor = Color(0xFF1A2A38)
)

val BlueSky: UserPreference = UserPreference(BlueSkyLight)

/**
 * Converts a hex color string to a [TimelineCardPreference],
 * automatically adapting colors for dark or light theme.
 *
 * @param hexColor Optional hex color string for the theme.
 * @param isDarkTheme Whether dark theme styling should be applied.
 */
fun colorHexToCardPreference(hexColor: String?, isDarkTheme: Boolean = false): TimelineCardPreference {
    val baseColor = hexColor
        ?.runCatching { Color(this.toColorInt()) }
        ?.getOrNull()
        ?: return if (isDarkTheme) BlueSkyDark else BlueSkyLight

    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(baseColor.toArgb(), hsv)

    return if (isDarkTheme) {
        // In dark mode: ensure baseColor has enough luminance for contrast
        val darkThemeHsv = hsv.clone().apply {
            this[1] = (this[1] * 0.85f).coerceIn(0.25f, 0.75f)
            this[2] = this[2].coerceAtLeast(0.78f)
        }
        val adjustedAccent = Color(android.graphics.Color.HSVToColor(darkThemeHsv))

        // Create subtle tinted secondary surface
        val darkSurfaceHsv = hsv.clone().apply {
            this[1] = (this[1] * 0.35f).coerceIn(0.12f, 0.40f)
            this[2] = 0.22f
        }
        val secondarySurface = Color(android.graphics.Color.HSVToColor(darkSurfaceHsv))

        TimelineCardPreference(
            primaryTextColor = adjustedAccent,
            surfaceColor = Color(0xFF1E1E1E),
            secondaryTextColor = Color(0xFFE6E6E6),
            subtitleTextColor = Color(0xFFA5A5A5),
            secondarySurfaceColor = secondarySurface,
            iconColor = adjustedAccent
        )
    } else {
        // Lighten via HSV: push value to ~0.97, drop saturation to ~0.18
        val lightHsv = hsv.clone().apply {
            this[1] = this[1] * 0.18f
            this[2] = 0.97f
        }
        val lightColor = Color(android.graphics.Color.HSVToColor(lightHsv))

        TimelineCardPreference(
            primaryTextColor = baseColor,
            surfaceColor = Color.White,
            secondaryTextColor = Color(0xFF333333),
            subtitleTextColor = LightGrey,
            secondarySurfaceColor = lightColor,
            iconColor = baseColor
        )
    }
}

@Composable
fun rememberTimelineCardPreference(
    hexColor: String?,
    isDarkTheme: Boolean = isSystemInDarkTheme()
): TimelineCardPreference {
    return remember(hexColor, isDarkTheme) {
        colorHexToCardPreference(hexColor, isDarkTheme)
    }
}
