package com.personalproject.mydiary.domain.timeline.model

/**
 * Defines the frequency at which a recurring event repeats.
 * This only applies to Events and Tasks.
 */
enum class Recurrence {
    NONE,
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
    CUSTOM
}