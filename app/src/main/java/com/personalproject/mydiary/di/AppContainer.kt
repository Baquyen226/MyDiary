package com.personalproject.mydiary.di

import com.personalproject.mydiary.data.mock.MockEntryRepository
import com.personalproject.mydiary.data.mock.MockFolderRepository
import com.personalproject.mydiary.data.mock.MockNoteRepository
import com.personalproject.mydiary.data.mock.MockProfileRepository
import com.personalproject.mydiary.domain.folder.FolderRepository
import com.personalproject.mydiary.domain.profile.ProfileRepository
import com.personalproject.mydiary.domain.timeline.EntryRepository
import com.personalproject.mydiary.domain.timeline.NoteRepository

interface AppContainer {
    val folderRepository: FolderRepository
    val profileRepository: ProfileRepository
    val entryRepository: EntryRepository
    val noteRepository: NoteRepository
}

class DefaultAppContainer : AppContainer {
    override val folderRepository: FolderRepository by lazy { MockFolderRepository() }
    override val profileRepository: ProfileRepository by lazy { MockProfileRepository() }
    override val entryRepository: EntryRepository by lazy { MockEntryRepository() }
    override val noteRepository: NoteRepository by lazy { MockNoteRepository() }
}
