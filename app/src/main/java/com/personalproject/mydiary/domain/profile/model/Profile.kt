package com.personalproject.mydiary.domain.profile.model

/**
 * Represents user profile information.
 *
 * @property id Unique identifier for the user profile.
 * @property name Display name of the user.
 * @property location Optional location or status text.
 * @property note Optional personal bio or note.
 */
data class Profile(
    val id: String? = null,
    val name: String? = null,
    val location: String? = null,
    val note: String? = null
) {
    /**
     * Backward-compatible alias for [id].
     */
    val uuid: String? get() = id
}
