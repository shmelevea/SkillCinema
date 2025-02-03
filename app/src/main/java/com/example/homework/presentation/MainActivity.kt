package com.example.homework.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.homework.R
import com.example.homework.data.PreferencesManager
import com.example.homework.presentation.onboarding.OnboardingFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val isFirstLaunch = preferencesManager.isFirstLaunch()

        if (isFirstLaunch) {
            val onboardingFragment = OnboardingFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, onboardingFragment)
                .commit()

            preferencesManager.setFirstLaunchCompleted()
        } else {
            val fragment = BasicFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit()
        }
    }
}