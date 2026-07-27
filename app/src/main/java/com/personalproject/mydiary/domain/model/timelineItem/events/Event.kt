package com.personalproject.mydiary.domain.model.timelineItem.events

import com.personalproject.mydiary.data.schema.timelineItem.Recurrence
import com.personalproject.mydiary.data.schema.timelineItem.TimelineEvent
import java.time.Instant

/**
 * Non-abstract [com.personalproject.mydiary.data.schema.timelineItem.TimelineEvent]. This depicts events that a regular user
 * may have.
 */
class Event (
    id: String,
    timestamp: Instant?,
    title: String,
    location: String?,
    tags: List<String>,
    recurrence: Recurrence,
    timelineStrRep: String
) : TimelineEvent(id, timestamp, title, location, tags, recurrence, timelineStrRep)