package com.personalproject.mydiary.domain.timelineItem.events



import java.time.Instant

/**
 * Non-abstract [TimelineEvent]. This depicts events that a regular user
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