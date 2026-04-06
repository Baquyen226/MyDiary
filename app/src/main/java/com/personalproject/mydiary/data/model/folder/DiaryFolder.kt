package com.personalproject.mydiary.data.model.folder

import com.personalproject.mydiary.data.model.timelineItem.DiaryEntry

class DiaryFolder (
    name: String,
    val diaryEntries : List<DiaryEntry>
) : Folder(name) {
}