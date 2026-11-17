package com.personalproject.mydiary.ui.feature.diary_entry

import androidx.annotation.DrawableRes
import com.personalproject.mydiary.R

@DrawableRes
fun Int.getMoodRes(): Int {
    return when (this) {
        1 -> R.drawable.ic_diary_mood_sad2
        2 -> R.drawable.ic_diary_mood_sad1
        3 -> R.drawable.ic_diary_mood_neutral
        4 -> R.drawable.ic_diary_mood_happy1
        5 -> R.drawable.ic_diary_mood_happy2
        else -> R.drawable.ic_diary_mood_not_set
    }
}
