package com.personalproject.mydiary.domain.timeline.model


/**
 * A specialized [TimelineItem] representing an actionable or scheduled occurrence.
 *
 * @property recurrence The frequency of recurrence.
 * @property timestampStringRepresentation Formatted string representation for the recurrence schedule.
 */
interface TimelineEvent : TimelineItem {
    val recurrence: Recurrence
    val timestampStringRepresentation: String?
    override val itemType: String get() = "EVENT"
}


