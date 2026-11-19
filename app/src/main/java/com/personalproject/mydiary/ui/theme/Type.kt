package com.personalproject.mydiary.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.os.ConfigurationCompat
import com.personalproject.mydiary.R

@OptIn(ExperimentalTextApi::class)
val defaultDisplayLargeFontFamily =
    FontFamily(
        Font(
            R.font.hiraginosansroundedw4,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(950),
                FontVariation.width(30f),
                FontVariation.slant(-6f),
            )
        )
    )

val chineseAestheticFontFamily =
    FontFamily(
        Font(R.font.hunninregular)
    )

val displayLargeFontFamily: FontFamily
    get() {
        return defaultDisplayLargeFontFamily
    }


val japaneseTextFontFamily = FontFamily(Font(R.font.hiraginosansw4))
val chineseTextFontFamily = FontFamily(Font(R.font.hunninregular))
val defaultTextFontFamily = FontFamily.Default


@Composable
fun getAppTextFontFamily(): FontFamily {
    val configuration = LocalConfiguration.current
    val currentLocale = ConfigurationCompat.getLocales(configuration).get(0)
    return when (currentLocale?.language?.lowercase()) {
        "zh" -> chineseTextFontFamily
        "ja" -> japaneseTextFontFamily
        else -> defaultTextFontFamily
    }
}


val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)


@Composable
fun getAppTypography(): Typography {
    val textFont = getAppTextFontFamily()
    return Typography(
        bodyLarge = TextStyle(
            fontFamily = textFont,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = textFont,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = textFont,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        ),
        titleLarge = TextStyle(
            fontFamily = textFont,
            fontWeight = FontWeight.Normal,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = textFont,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = textFont,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        labelLarge = TextStyle(
            fontFamily = textFont,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = textFont,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = textFont,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        )
    )
}
