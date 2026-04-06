package com.personalproject.mydiary.data.model.folder

import com.personalproject.mydiary.data.model.timelineItem.TimelineEvent

class EventFolder (
    name : String,
    val eventList : List<TimelineEvent>
) : Folder(name) {
}