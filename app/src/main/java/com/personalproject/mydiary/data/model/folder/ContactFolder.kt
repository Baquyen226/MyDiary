package com.personalproject.mydiary.data.model.folder

import com.personalproject.mydiary.data.model.person.Contact

class ContactFolder (
    name : String,
    val eventList : List<Contact>
) : Folder(name) {
}