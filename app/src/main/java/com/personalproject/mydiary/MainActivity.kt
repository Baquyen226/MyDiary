package com.personalproject.mydiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.personalproject.mydiary.data.timelineItem.MockDiary
import com.personalproject.mydiary.ui.feature_diary_entry.component.DiaryEntryCard
import com.personalproject.mydiary.ui.feature_diary_entry.component.DiaryEntryWindow
import com.personalproject.mydiary.ui.theme.MyDiaryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyDiaryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(start = 4.dp, end = 4.dp)) {
                        Greeting(
                            name = "Android",
                            modifier = Modifier.padding(innerPadding)
                        )
//                        DiaryEntryCard(MockDiary.mockDiaryEntry_2)
//                        DiaryEntryCard(MockDiary.mockDiaryEntry_3_JP)
                        DiaryEntryWindow(entry = MockDiary.mockDiaryEntry_1)
                    }
                }
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