package com.personalproject.mydiary.ui.feature.folder_view

import com.personalproject.mydiary.core.util.LocationUtil
import com.personalproject.mydiary.data.mock.MockEntryRepository
import com.personalproject.mydiary.data.mock.MockNoteRepository
import com.personalproject.mydiary.domain.timeline.model.DiaryEntry
import com.personalproject.mydiary.domain.timeline.model.NoteEntry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.time.Instant

class DiaryEntryLocationTest {

    private val entryRepository = MockEntryRepository()
    private val noteRepository = MockNoteRepository()

    @Test
    fun testAddEntryWithResolvedLocation() = runBlocking {
        val entry = DiaryEntry(
            id = "test_entry_1",
            folderId = "folder_1",
            timestamp = Instant.now(),
            title = "Tokyo Trip",
            content = "Exploring Shibuya.",
            location = "Tokyo, Japan"
        )
        entryRepository.addEntry(entry)

        val entries = entryRepository.observeEntriesFromFolder("folder_1").first()
        val found = entries.find { it.id == "test_entry_1" }
        assertNotNull(found)
        assertEquals("Tokyo, Japan", found?.location)
    }

    @Test
    fun testAddEntryWithUnknownLocation() = runBlocking {
        val entry = DiaryEntry(
            id = "test_entry_2",
            folderId = "folder_1",
            timestamp = Instant.now(),
            title = "Quiet Day",
            content = "At home.",
            location = LocationUtil.UNKNOWN_LOCATION
        )
        entryRepository.addEntry(entry)

        val entries = entryRepository.observeEntriesFromFolder("folder_1").first()
        val found = entries.find { it.id == "test_entry_2" }
        assertNotNull(found)
        assertEquals("UNKNOWN", found?.location)
    }

    @Test
    fun testAddNoteWithLocation() = runBlocking {
        val note = NoteEntry(
            id = "test_note_1",
            folderId = "folder_1",
            timestamp = Instant.now(),
            title = "Meeting spot",
            content = "Station cafe",
            location = "Shinjuku, Tokyo"
        )
        noteRepository.addEntry(note)

        val notes = noteRepository.observeEntriesFromFolder("folder_1").first()
        val found = notes.find { it.id == "test_note_1" }
        assertNotNull(found)
        assertEquals("Shinjuku, Tokyo", found?.location)
    }

    @Test
    fun testLocationConstantMatchesStrings() {
        assertEquals("UNKNOWN", LocationUtil.UNKNOWN_LOCATION)
    }
}
