package com.personalproject.mydiary.data.model.person

import android.location.Location

class Contact (
    var id : Int,
    name: String,
    birthday: String?,
    note: String?,
    location: Location?
) : Person (name, birthday, note, location) {

}