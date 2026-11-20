package com.personalproject.mydiary.domain.profile.model

/**
 * Represents user profile information.
 *
 * @property id Unique identifier for the user profile.
 * @property name Display name of the user.
 * @property location Optional location or status text.
 * @property note Optional personal bio or note.
 * @property phoneNumber Optional phone number.
 * @property avatar Optional avatar image URI or path.
 * @property banner Optional banner image URI, path, or color hex.
 * @property themeColor Optional theme accent color hex for the folder list and app screens.
 */
data class Profile(
    val id: String? = null,
    val name: String? = null,
    val location: String? = null,
    val note: String? = null,
    val phoneNumber: String? = null,
    val avatar: String? = null,
    val banner: String? = null,
    val themeColor: String? = null
) {
    /**
     * Backward-compatible alias for [id].
     */
    val uuid: String? get() = id

    /**
     * Backward-compatible alias for [avatar].
     */
    val avatarUri: String? get() = avatar

    /**
     * Backward-compatible alias for [banner].
     */
    val bannerUri: String? get() = banner

    /**
     * Backward-compatible alias for [banner].
     */
    val background: String? get() = banner
    val backgroundUri: String? get() = banner
}
