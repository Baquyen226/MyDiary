package com.personalproject.mydiary.domain.timeline.model.enums

/**
 * RFC 5545
 */
enum class Recurrence(val rrule: String?) {
    NONE(null),
    DAILY("RRULE:FREQ=DAILY"),
    WEEKLY("RRULE:FREQ=WEEKLY"),
    MONTHLY("RRULE:FREQ=MONTHLY"),
    YEARLY("RRULE:FREQ=YEARLY"),
    CUSTOM(null);

    companion object {
        fun fromRRule(rruleString: String?): Recurrence {
            if (rruleString.isNullOrBlank()) return NONE
            val normalized = rruleString.trim().uppercase()
            return when {
                normalized.contains("FREQ=DAILY") -> DAILY
                normalized.contains("FREQ=WEEKLY") -> WEEKLY
                normalized.contains("FREQ=MONTHLY") -> MONTHLY
                normalized.contains("FREQ=YEARLY") -> YEARLY
                normalized.startsWith("RRULE:") -> CUSTOM
                else -> NONE
            }
        }
    }
}