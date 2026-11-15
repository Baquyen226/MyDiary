package com.personalproject.mydiary.domain.timeline.model

import java.time.Instant

/**
 * Base representation of an item within a chronological feed.
 *
 * @property id A unique identifier for the item.
 * @property timestamp The date/time at which the item occurred.
 * @property title A brief headline or summary of the item.
 * @property location An optional physical location or coordinate string.
 * @property itemType The category/type of the timeline item (e.g. "DIARY", "EVENT", "TASK").
 */
interface TimelineItem {
    val id: String
    val folderId: String?
    val timestamp: Instant?
    val title: String
    val location: String?
    val itemType: String
}
