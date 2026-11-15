package com.personalproject.mydiary.domain.folder.model

/**
 * Represents a folder that categorizes diary entries, notes, events, or contacts.
 *
 * @property id Unique identifier for the folder.
 * @property name Display name of the folder.
 * @property type The [FolderType] indicating what kind of content this folder holds.
 * @property color Optional hex color string for custom folder styling.
 */
data class Folder(
    val id: String,
    val name: String,
    val type: FolderType,
    val color: String? = null
) {
    /**
     * Backward-compatible alias for [id].
     */
    val uuid: String get() = id
}
