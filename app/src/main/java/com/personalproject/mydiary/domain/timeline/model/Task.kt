package com.personalproject.mydiary.domain.timeline.model

import com.personalproject.mydiary.domain.timeline.model.enums.Recurrence
import java.time.Instant

/**
 * An actionable reminder or task on the timeline, compliant with RFC 5545 VTODO.
 *
 * @property id Unique identifier.
 * @property folderId Parent folder ID.
 * @property timestamp Creation or target instant.
 * @property dueDate Optional deadline instant (maps to DUE).
 * @property title Task headline/summary.
 * @property location Optional location reference.
 * @property recurrence Recurrence frequency.
 * @property rrule Raw RFC 5545 recurrence rule (e.g. "RRULE:FREQ=DAILY").
 * @property timestampStringRepresentation Formatted string representation.
 * @property isCompleted Whether the task has been checked off.
 */
data class Task(
    override val id: String,
    override val folderId: String?,
    override val timestamp: Instant?,
    val dueDate: Instant? = timestamp,
    override val title: String,
    override val location: String? = null,
    override val recurrence: Recurrence = Recurrence.NONE,
    override val rrule: String? = recurrence.rrule,
    override val timestampStringRepresentation: String? = null,
    val isCompleted: Boolean = false
) : TimelineEvent
