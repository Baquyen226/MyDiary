package com.personalproject.mydiary.domain.timeline

import com.personalproject.mydiary.domain.timeline.model.DiaryEntry
import kotlinx.coroutines.flow.Flow

interface EntryRepository
{
    fun getEntries()

    fun getEntriesFromFolder(folderName: String): Flow<DiaryEntry?>

    fun addEntries(entry: DiaryEntry)

    fun deleteEntry(id: String)
}