package com.example.homework

import android.app.Application
import android.content.Context
import com.example.homework.utils.KEY_MOVIES_FETCHED
import com.example.homework.utils.PREFS_NAME
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean(KEY_MOVIES_FETCHED, false)
            apply()
        }
    }
}