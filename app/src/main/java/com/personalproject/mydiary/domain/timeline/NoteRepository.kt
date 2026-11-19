package com.personalproject.mydiary.domain.timeline

import com.personalproject.mydiary.domain.timeline.model.NoteEntry
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getEntries(): Flow<List<NoteEntry>>

    fun observeEntriesFromFolder(folderId: String): Flow<List<NoteEntry>>

    fun getEntryById(id: String): Flow<NoteEntry?>

    fun addEntry(noteEntry: NoteEntry)

    fun update(noteEntry: NoteEntry)

    fun deleteEntry(id: String)
}