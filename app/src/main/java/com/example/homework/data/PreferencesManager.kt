package com.example.homework.data

import android.content.SharedPreferences
import com.example.homework.utils.IS_FIRST_LAUNCH
import javax.inject.Inject

class PreferencesManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(IS_FIRST_LAUNCH, true)
    }

    fun setFirstLaunchCompleted() {
        sharedPreferences.edit().putBoolean(IS_FIRST_LAUNCH, false).apply()
    }
}