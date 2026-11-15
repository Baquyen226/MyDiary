package com.personalproject.mydiary.domain.timeline.model

import java.time.Instant

/**
 * An actionable reminder or task on the timeline.
 */
data class Task(
    override val id: String,
    override val folderId: String?,
    override val timestamp: Instant?,
    override val title: String,
    override val location: String? = null,
    override val recurrence: Recurrence = Recurrence.NONE,
    override val timestampStringRepresentation: String? = null,
    val isCompleted: Boolean = false
) : TimelineEvent