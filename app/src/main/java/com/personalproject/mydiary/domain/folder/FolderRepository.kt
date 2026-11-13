package com.personalproject.mydiary.domain.folder

import com.personalproject.mydiary.domain.folder.model.Folder
import com.personalproject.mydiary.domain.folder.model.FolderType
import kotlinx.coroutines.flow.Flow


interface FolderRepository {
    fun getFolders(): Flow<List<Folder>>

    fun getFolderByUuid(uuid: String): Flow<Folder?>

    suspend fun createFolder(name: String, type: FolderType)

    suspend fun renameFolder(uuid: String, newName: String)

    suspend fun deleteFolder(uuid: String)

    fun getFoldersByType(type: FolderType): Flow<List<Folder>>
}