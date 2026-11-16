package com.personalproject.mydiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.personalproject.mydiary.ui.navigation.AppNavHost
import com.personalproject.mydiary.ui.theme.MyDiaryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyDiaryTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }
    }
}
