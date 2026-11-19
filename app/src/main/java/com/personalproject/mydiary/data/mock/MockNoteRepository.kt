package com.personalproject.mydiary.data.mock

import com.personalproject.mydiary.domain.timeline.NoteRepository
import com.personalproject.mydiary.domain.timeline.model.NoteEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class MockNoteRepository : NoteRepository {
    private val _entries = MutableStateFlow(
        listOf(
            MockEntry.mockNoteEntry_1,
            MockEntry.mockNoteEntry_2
        )
    )

    override fun getEntries(): Flow<List<NoteEntry>> {
        return _entries.asStateFlow()
    }

    override fun observeEntriesFromFolder(folderId: String): Flow<List<NoteEntry>> {
        return _entries.map { entries ->
            entries
                .filter { it.folderId == folderId }
                .sortedWith(
                    compareByDescending<NoteEntry> { it.isPinned }
                        .thenByDescending { it.timestamp ?: it.createdAt }
                )
        }
    }

    override fun getEntryById(id: String): Flow<NoteEntry?> {
        return _entries.map { entries -> entries.find { it.id == id } }
    }

    override fun addEntry(noteEntry: NoteEntry) {
        val resolvedEntry = if (noteEntry.title.isBlank()) {
            noteEntry.copy(title = "No Title")
        } else {
            noteEntry.copy(title = noteEntry.title.trim())
        }
        _entries.value = _entries.value.filterNot { it.id == resolvedEntry.id } + resolvedEntry
    }

    override fun update(noteEntry: NoteEntry) {
        _entries.value = _entries.value.map { existing ->
            if (existing.id == noteEntry.id) noteEntry else existing
        }
    }

    override fun deleteEntry(id: String) {
        _entries.value = _entries.value.filterNot { it.id == id }
    }
}
