package com.personalproject.mydiary.data.mock

import com.personalproject.mydiary.domain.timeline.EntryRepository
import com.personalproject.mydiary.domain.timeline.model.DiaryEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class MockEntryRepository : EntryRepository {
    private val _entries = MutableStateFlow(
        listOf(
            MockEntry.mockDiaryEntry_1,
            MockEntry.mockDiaryEntry_2,
            MockEntry.mockDiaryEntry_3,
            MockEntry.mockDiaryEntry_3_JP,
            MockEntry.mockDiaryEntry_5,
            MockEntry.mockDiaryEntry_6,
            MockEntry.mockDiaryEntry_7,
            MockEntry.mockDiaryEntry_8
        )
    )

    override fun getEntries(): Flow<List<DiaryEntry>> {
        return _entries.asStateFlow()
    }

    override fun update(entry: DiaryEntry) {
        _entries.value = _entries.value.map { existing ->
            if (existing.id == entry.id) entry else existing
        }
    }

    override fun observeEntriesFromFolder(folderId: String): Flow<List<DiaryEntry>> {
        return _entries.map { entries ->
            entries
                .filter { it.folderId == folderId }
                .sortedByDescending { it.timestamp }
        }
    }

    override fun getEntryById(id: String): Flow<DiaryEntry?> {
        return _entries.map { entries -> entries.find { it.id == id } }
    }

    override fun addEntry(entry: DiaryEntry) {
        val resolvedEntry = if (entry.title.isBlank()) {
            entry.copy(title = "No Title")
        } else {
            entry.copy(title = entry.title.trim())
        }

        _entries.value = _entries.value.filterNot { it.id == resolvedEntry.id } + resolvedEntry
    }
    override fun deleteEntry(id: String) {
        _entries.value = _entries.value.filterNot { it.id == id }
    }
}
