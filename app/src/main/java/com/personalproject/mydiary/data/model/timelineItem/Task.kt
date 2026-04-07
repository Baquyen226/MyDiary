package com.personalproject.mydiary.data.model.timelineItem

import com.personalproject.mydiary.data.model.person.Contact

import java.time.Instant

class Task (
    id: String,
    timestamp: Instant?,
    title: String,
    location: String?,
    tags: List<Contact>,
    recurrence: Recurrence,
    timelineStrRep: String
) : TimelineEvent(id, timestamp, title, location, tags, recurrence, timelineStrRep){
    override fun render() {
        TODO("Not yet implemented")
    }

    override fun fromString(input: String) {
        TODO("Not yet implemented")
    }
}