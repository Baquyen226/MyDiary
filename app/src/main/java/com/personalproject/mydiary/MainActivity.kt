package com.personalproject.mydiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.personalproject.mydiary.data.mock.MockDiary
import com.personalproject.mydiary.ui.feature_diary_entry.components.DiaryEntryCard
import com.personalproject.mydiary.ui.feature_diary_entry.components.DiaryEntryWindow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.personalproject.mydiary.data.folder.MockFolderRepository
import com.personalproject.mydiary.data.mock.MockProfileRepository
import com.personalproject.mydiary.ui.feature_folder_list.FolderListScreen
import com.personalproject.mydiary.ui.feature_folder_list.FolderListViewModel
import com.personalproject.mydiary.ui.theme.MyDiaryTheme
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val folderRepository = MockFolderRepository()
        val profileRepository = MockProfileRepository()
        
        enableEdgeToEdge()
        setContent {
            MyDiaryTheme {
                val viewModel: FolderListViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return FolderListViewModel(folderRepository, profileRepository) as T
                        }
                    }
                )

                FolderListScreen(
                    viewModel = viewModel,
                    onFolderClick = { uuid ->
                        // Handle folder click
                    }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )

    Column(modifier = Modifier.padding(start = 4.dp, end = 4.dp)) {
        DiaryEntryCard()
        DiaryEntryCard(MockDiary.mockDiaryEntry_2)
        DiaryEntryCard(MockDiary.mockDiaryEntry_3_JP)
        DiaryEntryWindow(entry = MockDiary.mockDiaryEntry_1)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyDiaryTheme {
        Greeting("Android")
        DiaryEntryCard()
    }
}
