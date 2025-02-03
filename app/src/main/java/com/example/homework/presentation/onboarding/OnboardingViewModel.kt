package com.example.homework.presentation.onboarding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {

    private val _currentPagePosition = MutableLiveData<Int>()

    fun setCurrentPagePosition(position: Int) {
        _currentPagePosition.value = position
    }
}