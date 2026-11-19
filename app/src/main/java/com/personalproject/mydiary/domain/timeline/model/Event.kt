package com.personalproject.mydiary.domain.timeline.model

import com.personalproject.mydiary.domain.timeline.model.enums.Recurrence
import java.time.Instant

/**
 * A scheduled calendar event on the timeline, compliant with RFC 5545 (iCalendar) VEVENT
 * and Google Calendar event models.
 *
 * @property id Unique identifier.
 * @property folderId Parent folder ID.
 * @property timestamp Start instant of the event (maps to DTSTART).
 * @property endTime Optional end instant of the event (maps to DTEND).
 * @property isAllDay Whether this is a full-day event without specific clock times.
 * @property title Event title (maps to SUMMARY).
 * @property location Physical or virtual event location.
 * @property description Optional detailed description.
 * @property recurrence Recurrence frequency.
 * @property rrule Raw RFC 5545 recurrence rule (e.g. "RRULE:FREQ=WEEKLY;BYDAY=TU").
 * @property timestampStringRepresentation Formatted string representation for the schedule.
 */
data class Event(
    override val id: String,
    override val folderId: String?,
    override val timestamp: Instant?,
    val endTime: Instant? = null,
    val isAllDay: Boolean = false,
    override val title: String,
    override val location: String? = null,
    val description: String? = null,
    override val recurrence: Recurrence = Recurrence.NONE,
    override val rrule: String? = recurrence.rrule,
    override val timestampStringRepresentation: String? = null
) : TimelineEvent
