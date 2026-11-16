package com.personalproject.mydiary.domain.timeline

import com.personalproject.mydiary.domain.timeline.model.DiaryEntry
import kotlinx.coroutines.flow.Flow

interface EntryRepository {
    fun getEntries(): Flow<List<DiaryEntry>>

    fun observeEntriesFromFolder(folderId: String): Flow<List<DiaryEntry>>

    fun getEntryById(id: String): Flow<DiaryEntry?>

    fun addEntry(entry: DiaryEntry)

    fun update(entry: DiaryEntry)

    fun deleteEntry(id: String)
}
