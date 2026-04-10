package com.personalproject.mydiary.data.model.timelineItem

import com.personalproject.mydiary.data.model.person.Contact

import java.time.Instant

/**
 * A specific property in [TimelineItem] uses the following class to describe
 * how the user feels about their day.
 */
enum class Mood {
    ABYSMAL, SAD, ANGRY, ANNOYED, NEUTRAL, MILD, FOND, HAPPY, SUPERCALIFRAGILISTICEXPIALIDOCIOUS
}

/**
 * A concrete implementation of [TimelineItem] representing a personal journal or diary log.
 *
 * This class captures detailed textual content and allows for marking entries as favorites
 * via the [isBookmarked] flag. A diary entry should have two ways of showing it, either through
 * a card for brief summary, or the entire window.
 *
 *
 * @property description A brief summary or subtitle of the entry.
 * @property content The main body text of the diary entry.
 * @property mood How the user feels about their day.
 * @property isBookmarked True if the entry has been starred or pinned by the user.
 */
class DiaryEntry (
    id: String,
    timestamp: Instant?,
    title: String,
    location: String?,
    tags: List<Contact>,
    var description: String?,
    var content: String?,
    var mood: Mood = Mood.NEUTRAL,
    var isBookmarked: Boolean
) : TimelineItem(id, timestamp, title, location, tags) {
    override fun render() {}

    override fun fromString(input: String) {}
}