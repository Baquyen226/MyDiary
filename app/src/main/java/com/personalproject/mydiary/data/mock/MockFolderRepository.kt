package com.personalproject.mydiary.data.mock

import com.personalproject.mydiary.domain.folder.FolderRepository
import com.personalproject.mydiary.domain.folder.model.Folder
import com.personalproject.mydiary.domain.folder.model.FolderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockFolderRepository : FolderRepository {

    private val mockFolders = listOf(
        Folder(
            uuid = "1",
            name = "緊急時以外かけちゃダメ",
            type = FolderType.CONTACTS,
            color = null // Use default color
        ),
        Folder(
            uuid = "2",
            name = "DIARY",
            type = FolderType.DIARY,
            color = null
        ),
        Folder(
            uuid = "3",
            name = "みつはのにっき",
            type = FolderType.DIARY,
            color = "#E57373"
        ),
        Folder(
            uuid = "4",
            name = "禁止事項",
            type = FolderType.NOTES,
            color = null
        ),
        Folder(
            uuid = "5",
            name = "行きたいおみせメモ",
            type = FolderType.NOTES,
            color = "#E57373"
        )
    )

    // WHAT DOES ANY OF THIS DO
    override fun getFolders(): Flow<List<Folder>> {
        return flowOf(mockFolders);
    }

    override fun getFolderByUuid(uuid: String): Flow<Folder?> {
        TODO("Not yet implemented")
    }

    override suspend fun createFolder(
        name: String,
        type: FolderType
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun renameFolder(uuid: String, newName: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFolder(uuid: String) {
        TODO("Not yet implemented")
    }

    override fun getFoldersByType(type: FolderType): Flow<List<Folder>> {
        TODO("Not yet implemented")
    }
}