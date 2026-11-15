package com.personalproject.mydiary.domain.timeline.model

import java.time.Instant

/**
 * A scheduled calendar event on the timeline.
 */
data class Event(
    override val id: String,
    override val folderId: String?,
    override val timestamp: Instant?,
    override val title: String,
    override val location: String? = null,
    override val recurrence: Recurrence = Recurrence.NONE,
    override val timestampStringRepresentation: String? = null
) : TimelineEvent