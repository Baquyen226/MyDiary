package com.personalproject.mydiary.domain.timeline.model

import com.personalproject.mydiary.domain.timeline.model.enums.Recurrence
import com.personalproject.mydiary.domain.timeline.model.enums.ReminderTargetType
import java.time.Instant
import java.util.UUID



/**
 * Represents a scheduled push notification or alarm, decoupled from its parent entity.
 * Aligned with Google Calendar reminders and the iCalendar (RFC 5545) VALARM specification.
 *
 * @property id Unique identifier for the reminder.
 * @property targetId ID of the parent [NoteEntry], [Task], or [Event].
 * @property targetType The type of entity this reminder belongs to.
 * @property title Notification headline/summary.
 * @property message Optional subtitle or preview text.
 * @property triggerTime The exact [Instant] when the Android alarm and notification must fire.
 * @property minutesBefore Relative offset before an event start (matches Google Calendar `minutes`
 *                         and RFC 5545 `VALARM TRIGGER:-PT{m}M`). Null for absolute one-shot alarms.
 * @property recurrence The frequency of recurrence for this reminder.
 * @property rrule Raw RFC 5545 recurrence rule string (e.g. "RRULE:FREQ=WEEKLY;BYDAY=TU").
 * @property isEnabled Whether this alarm is currently active in the system.
 */
data class Reminder(
    val id: String = UUID.randomUUID().toString(),
    val targetId: String,
    val targetType: ReminderTargetType,
    val title: String,
    val message: String? = null,
    val triggerTime: Instant,
    val minutesBefore: Int? = null,
    val recurrence: Recurrence = Recurrence.NONE,
    val rrule: String? = recurrence.rrule,
    val isEnabled: Boolean = true
) {
    /**
     * Converts relative minutesBefore into an RFC 5545 VALARM TRIGGER string
     * (e.g. 15 minutes -> "-PT15M", 2 hours -> "-PT120M").
     */
    fun toValarmTriggerString(): String? {
        val minutes = minutesBefore ?: return null
        return if (minutes == 0) "PT0S" else "-PT${minutes}M"
    }

    companion object {
        /**
         * Creates a reminder relative to an event/task start time (e.g. 15 minutes before)
         */
        fun createRelative(
            targetId: String,
            targetType: ReminderTargetType,
            eventStartTime: Instant,
            minutesBefore: Int,
            title: String,
            message: String? = null,
            recurrence: Recurrence = Recurrence.NONE,
            rrule: String? = recurrence.rrule
        ): Reminder {
            val trigger = eventStartTime.minusSeconds(minutesBefore * 60L)
            return Reminder(
                targetId = targetId,
                targetType = targetType,
                title = title,
                message = message,
                triggerTime = trigger,
                minutesBefore = minutesBefore,
                recurrence = recurrence,
                rrule = rrule,
                isEnabled = true
            )
        }

        /**
         * Parses an RFC 5545 iCalendar VALARM trigger string (like "-PT15M" or "-P1D")
         * into relative minutes before the event start.
         */
        fun parseValarmMinutes(triggerString: String?): Int? {
            if (triggerString.isNullOrBlank()) return null
            val upper = triggerString.trim().uppercase()
            if (upper == "PT0S" || upper == "PT0M") return 0

            return runCatching {
                when {
                    upper.contains("M") && upper.contains("PT") -> {
                        val num = upper.substringAfter("PT").substringBefore("M").toInt()
                        num
                    }
                    upper.contains("H") && upper.contains("PT") -> {
                        val hours = upper.substringAfter("PT").substringBefore("H").toInt()
                        hours * 60
                    }
                    upper.contains("D") && upper.contains("P") -> {
                        val days = upper.substringAfter("P").substringBefore("D").replace("-", "").toInt()
                        days * 1440
                    }
                    else -> null
                }
            }.getOrNull()
        }
    }
}
