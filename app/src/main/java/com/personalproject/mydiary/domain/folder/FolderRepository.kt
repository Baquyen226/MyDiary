package com.personalproject.mydiary.domain.folder

import com.personalproject.mydiary.domain.folder.model.Folder
import com.personalproject.mydiary.domain.folder.model.FolderType
import kotlinx.coroutines.flow.Flow

interface FolderRepository {
    fun getFolders(): Flow<List<Folder>>

    fun getFolderById(id: String): Flow<Folder?>

    suspend fun createFolder(name: String, type: FolderType, color: String? = null)

    suspend fun renameFolder(id: String, newName: String)

    suspend fun deleteFolder(id: String)

    fun getFoldersByType(type: FolderType): Flow<List<Folder>>
}
