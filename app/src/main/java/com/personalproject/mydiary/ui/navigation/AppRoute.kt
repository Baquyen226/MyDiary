package com.personalproject.mydiary.ui.navigation

object AppRoute {
    const val folderListScreen = "folderListScreen"
    const val folderViewScreen = "folderViewScreen"

    fun folderView(folderId: String): String = "$folderViewScreen/$folderId"
}
