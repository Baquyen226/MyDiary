package com.personalproject.mydiary.data.model.person

import android.location.Location

/**
 * Generic representation a person. This can either be [Contact] or [User]
 * @property name: The name of the person.
 * @property birthday: The birthday of the person. This can be null.
 * @property note: Notes regarding this individual.
 * @property location: The location of this person.
 */
open class Person (
    var name: String,
    var birthday: String?,
    var note: String?,
    var location: Location?
)