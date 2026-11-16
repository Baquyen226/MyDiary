package com.personalproject.mydiary.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.personalproject.mydiary.ui.feature.folder_list.FolderListScreen
import com.personalproject.mydiary.ui.feature.folder_list.FolderListViewModel
import com.personalproject.mydiary.ui.feature.folder_view.FolderViewScreen
import com.personalproject.mydiary.ui.feature.folder_view.FolderViewViewModel

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.folderListScreen,
        modifier = modifier
    ) {
        composable(AppRoute.folderListScreen) {
            val viewModel: FolderListViewModel = viewModel(factory = FolderListViewModel.Factory)
            FolderListScreen(
                viewModel = viewModel,
                onFolderClick = { folderId ->
                    navController.navigate(AppRoute.folderView(folderId))
                }
            )
        }

        composable(
            route = "${AppRoute.folderViewScreen}/{folderId}",
            arguments = listOf(
                navArgument("folderId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val folderId = backStackEntry.arguments?.getString("folderId") ?: ""
            val viewModel: FolderViewViewModel = viewModel(
                factory = FolderViewViewModel.provideFactory(folderId)
            )
            FolderViewScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
