package com.personalproject.mydiary.data.model.person

import android.location.Location
import com.personalproject.mydiary.data.model.folder.DiaryFolder
import com.personalproject.mydiary.data.model.folder.EventFolder
import com.personalproject.mydiary.data.model.folder.Folder

/**
 * The main user of the diary, is a [Person]. The user has a set of folders.
 * Folders can either contain a set of: diary entries, to-do list
 * (Time or Untimed), contacts and notes.
 *
 * @property name Name of the user
 * @property location Location of the user
 */
class User (
    name: String,
    birthday: String,
    note : String?,
    location: Location?,
    val folders: List<Folder>
) : Person (name, birthday, note, location) {
    val diaryFolders get() = folders.filterIsInstance<DiaryFolder>()
    val eventFolders get() = folders.filterIsInstance<EventFolder>()
}