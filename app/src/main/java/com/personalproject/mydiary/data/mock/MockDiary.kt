package com.personalproject.mydiary.data.mock

import com.personalproject.mydiary.data.model.person.Contact
import com.personalproject.mydiary.data.model.timelineItem.Mood
import com.personalproject.mydiary.data.model.timelineItem.DiaryEntry
import java.time.Instant

object MockDiary {
    val now: Instant = Instant.now()
    val yesterday: Instant = now.minusSeconds(86400)
    val eightDaysAgo: Instant = now.minusSeconds(86400 * 8)
    val yearsAgo: Instant = now.plusSeconds(86400 * 36).minusSeconds(86400 * 365);

    val mockDiaryEntry_1 = DiaryEntry(
        id = "D_000001",
        timestamp = now,
        title = "Test Diary 01",
        location = null,
        tags = emptyList<Contact>(),
        description = "This is a test!",
        content = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
        isBookmarked = false,
        mood = Mood.MILD
    )
}