package com.personalproject.mydiary.data.mock

import com.personalproject.mydiary.domain.folder.FolderRepository
import com.personalproject.mydiary.domain.folder.model.Folder
import com.personalproject.mydiary.domain.folder.model.FolderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

class MockFolderRepository : FolderRepository {

    private val _folders = MutableStateFlow(
        listOf(
            Folder(
                id = "1",
                name = "DIARY",
                type = FolderType.DIARY,
                color = null
            ),
            Folder(
                id = "2",
                name = "みつはのにっき",
                type = FolderType.DIARY,
                color = "#E57373"
            )
//            Folder(
//                id = "3",
//                name = "禁止事項",
//                type = FolderType.NOTES,
//                color = null
//            ),
//            Folder(
//                id = "4",
//                name = "行きたいおみせメモ",
//                type = FolderType.NOTES,
//                color = "#E57373"
//            ),
//            Folder(
//                id = "5",
//                name = "緊急時以外かけちゃダメ",
//                type = FolderType.CONTACTS,
//                color = null
//            )
        )
    )

    override fun getFolders(): Flow<List<Folder>> {
        return _folders.asStateFlow()
    }

    override fun getFolderById(id: String): Flow<Folder?> {
        return _folders.map { folders -> folders.find { it.id == id } }
    }

    override suspend fun createFolder(
        name: String,
        type: FolderType,
        color: String?
    ) {
        val newFolder = Folder(
            id = UUID.randomUUID().toString(),
            name = name,
            type = type,
            color = color
        )
        _folders.value += newFolder
    }

    override suspend fun renameFolder(id: String, newName: String) {
        _folders.value = _folders.value.map { folder ->
            if (folder.id == id) folder.copy(name = newName) else folder
        }
    }

    override suspend fun deleteFolder(id: String) {
        _folders.value = _folders.value.filterNot { it.id == id }
    }

    override fun getFoldersByType(type: FolderType): Flow<List<Folder>> {
        return _folders.map { folders -> folders.filter { it.type == type } }
    }
}
