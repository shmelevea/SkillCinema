package com.example.homework.data

import android.content.Context
import com.example.homework.utils.*

class ApiKeyProvider(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(PREFS_API_KEY, Context.MODE_PRIVATE)
    private val keys = listOf(
        "9637cb74-cf1b-4f2e-abab-8a99a395f41f",
        "518d733a-f25b-4fd1-8555-6d21940df124",
        "989140e3-b2a3-4f66-babb-818c11e79d6a",
        "1321b1dc-e4aa-41c8-9499-121b2388ded5",
        "86762d27-ac52-474a-a24c-4c9ce786ec10",
        "8fbf165a-029f-4ffe-816b-b581e41fea10",
        "c3d9d98d-b173-4549-86e8-933d30bb1ead",
        "ce5199d9-ad79-4392-9df5-deb4a271b937"
    )

    private var currentKeyIndex: Int
        get() = sharedPreferences.getInt(KEY_CURRENT_INDEX, 0)
        set(value) {
            sharedPreferences.edit().putInt(KEY_CURRENT_INDEX, value).apply()
        }

    val apiKey: String
        get() = keys[currentKeyIndex]

    fun switchKey() {
        currentKeyIndex = (currentKeyIndex + 1) % keys.size
    }
}