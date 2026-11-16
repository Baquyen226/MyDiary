package com.personalproject.mydiary

import android.app.Application
import com.personalproject.mydiary.di.AppContainer
import com.personalproject.mydiary.di.DefaultAppContainer

class MyDiaryApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}
