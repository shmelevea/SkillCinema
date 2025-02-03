package com.example.homework.presentation.search.year

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class YearSelectorViewModel @Inject constructor() : ViewModel() {

    private val _fromYearPage = MutableLiveData(0)
    val fromYearPage: LiveData<Int> get() = _fromYearPage

    private val _toYearPage = MutableLiveData(6)
    val toYearPage: LiveData<Int> get() = _toYearPage

    private val _fromYear = MutableLiveData<Int?>()
    val fromYear: LiveData<Int?> get() = _fromYear

    private val _toYear = MutableLiveData<Int?>()
    val toYear: LiveData<Int?> get() = _toYear

    fun setFromYear(year: Int?) {
        _fromYear.value = year
    }

    fun setToYear(year: Int?) {
        _toYear.value = year
    }

    fun setFromYearPage(page: Int) {
        _fromYearPage.value = page
    }

    fun setToYearPage(page: Int) {
        _toYearPage.value = page
    }
}