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
            MockDiary.mockDiaryEntry_1,
            MockDiary.mockDiaryEntry_2,
            MockDiary.mockDiaryEntry_3,
            MockDiary.mockDiaryEntry_3_JP
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
        _entries.value = _entries.value.filterNot { it.id == entry.id } + entry
    }

    override fun deleteEntry(id: String) {
        _entries.value = _entries.value.filterNot { it.id == id }
    }
}
