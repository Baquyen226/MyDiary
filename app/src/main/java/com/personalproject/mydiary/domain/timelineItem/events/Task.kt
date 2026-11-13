package com.personalproject.mydiary.domain.timelineItem.events



import java.time.Instant

class Task (
    id: String,
    timestamp: Instant?,
    title: String,
    location: String?,
    tags: List<String>,
    recurrence: Recurrence,
    timelineStrRep: String
) : TimelineEvent(id, timestamp, title, location, tags, recurrence, timelineStrRep)