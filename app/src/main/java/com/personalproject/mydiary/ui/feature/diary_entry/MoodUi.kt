package com.personalproject.mydiary.ui.feature.diary_entry

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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

@StringRes
fun Int.getMoodLabel(): Int {
    return when (this) {
        1 -> R.string.ui_diary_choose_mood1
        2 -> R.string.ui_diary_choose_mood2
        3 -> R.string.ui_diary_choose_mood3
        4 -> R.string.ui_diary_choose_mood4
        5 -> R.string.ui_diary_choose_mood5
        else -> R.string.ui_diary_choose_mood0
    }
}
