package com.personalproject.mydiary.data.model.timelineItem

import com.personalproject.mydiary.data.model.person.Contact

/**
 * Defines the frequency at which a [TimelineEvent] reoccurs.
 */
enum class Recurrence {
    NONE, DAILY, WEEKLY, MONTHLY, YEARLY, CUSTOM
}

/**
 * A specialized [TimelineItem] that represents an actionable or scheduled occurrence.
 * @property recurrence The length of recurrence cycle.
 * @property timestampStringRepresentation String representation of each recurrence
 * (Every X days/ Weekly on Wednesdays / Month...)
 */
abstract class TimelineEvent(
    id: String,
    timestamp: Long,
    title: String,
    location: String?,
    tags: List<Contact>,
    open var recurrence: Recurrence,
    var timestampStringRepresentation: String?
) : TimelineItem(id, timestamp, title, location, tags) {
}