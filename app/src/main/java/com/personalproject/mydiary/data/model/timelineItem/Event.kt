package com.personalproject.mydiary.data.model.timelineItem

import com.personalproject.mydiary.data.model.person.Contact

/**
 * Non-abstract [TimelineEvent]. This depicts events that a regular user
 * may have.
 */
class Event (
    id: String,
    timestamp: Long,
    title: String,
    location: String?,
    tags: List<Contact>,
    recurrence: Recurrence,
    timelineStrRep: String
) : TimelineEvent(id, timestamp, title, location, tags, recurrence, timelineStrRep) {
    override fun render() {
        TODO("Not yet implemented")
    }

    override fun fromString(input: String) {
        TODO("Not yet implemented")
    }
}