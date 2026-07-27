package com.personalproject.mydiary.data.schema.timelineItem

import com.personalproject.mydiary.domain.model.timelineItem.TimelineItem
import java.time.Instant

/**
 * Defines the frequency at which a [TimelineEvent] reoccurs.
 */
enum class Recurrence {
    NONE, DAILY, WEEKLY, MONTHLY, YEARLY, CUSTOM
}

/**
 * A specialized [com.personalproject.mydiary.domain.model.timelineItem.TimelineItem] that represents an actionable or scheduled occurrence.
 * @property recurrence The length of recurrence cycle.
 * @property timestampStringRepresentation String representation of each recurrence
 * (Every X days/ Weekly on Wednesdays / Month...)
 */
abstract class TimelineEvent(
    id: String,
    timestamp: Instant?,
    title: String,
    location: String?,
    tags: List<String>?,
    open var recurrence: Recurrence,
    var timestampStringRepresentation: String?
) : TimelineItem(id, timestamp, title, location, tags, itemType = "EVENT") {
}


