package com.personalproject.mydiary.domain.model.timelineItem

import java.time.Instant

/**
 * A base representation of an item within a chronological feed.
 * * This class serves as the foundation for all timeline-specific data models,
 * ensuring consistent metadata across different content types.
 *
 * @property id A unique identifier for the item.
 * @property timestamp The date at which the event occurred.
 * @property title A brief headline or summary of the item.
 * @property location An optional physical location or coordinate string associated with the item.
 * @property tags A list of [String] objects associated with this specific entry.
 */
abstract class TimelineItem (
    open val id: String,
    open val timestamp: Instant?,
    open val title: String,
    open val location: String?,
    open val tags: List<String>?,
    open val itemType: String?
)