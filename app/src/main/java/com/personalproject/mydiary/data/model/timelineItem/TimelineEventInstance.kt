package com.personalproject.mydiary.data.model.timelineItem

import com.personalproject.mydiary.data.model.person.Contact

import java.time.Instant

/**
 * A specific instance of [TimelineEvent]. Stores one recurrence of a [Task]
 * or [Event]. This should show on the calendar.
 */
class TimelineEventInstance (
    id: String,
    timestamp: Instant?,
    title: String,
    location: String?,
    tags: List<Contact>,
    var isCompleted: Boolean = false,
    var parentEventID: String
) : TimelineItem(id, timestamp, title, location, tags){
    override fun render() {
        TODO("Not yet implemented")
    }

    override fun fromString(input: String) {
        TODO("Not yet implemented")
    }
}