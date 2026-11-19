package com.personalproject.mydiary.domain.timeline.model

import com.personalproject.mydiary.domain.timeline.model.enums.Recurrence

/**
 * A specialized [TimelineItem] representing an actionable or scheduled occurrence,
 * compatible with RFC 5545 (iCalendar) and Google Calendar standards.
 *
 * @property recurrence The frequency of recurrence.
 * @property rrule Raw RFC 5545 recurrence rule string (e.g. "RRULE:FREQ=WEEKLY;BYDAY=TU").
 * @property timestampStringRepresentation Formatted string representation for the recurrence schedule.
 */
interface TimelineEvent : TimelineItem {
    val recurrence: Recurrence
    val rrule: String? get() = recurrence.rrule
    val timestampStringRepresentation: String?
    override val itemType: String get() = "EVENT"
}
