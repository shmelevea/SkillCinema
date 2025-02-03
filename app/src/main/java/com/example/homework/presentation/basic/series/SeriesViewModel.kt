package com.example.homework.presentation.basic.series

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework.data.remote.response.InfoById
import com.example.homework.data.remote.response.SeasonsItem
import com.example.homework.domain.CinemaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeriesViewModel @Inject constructor(
    private val repository: CinemaRepository
) : ViewModel() {

    private val _filmDetails = MutableLiveData<InfoById>()
    val filmDetails: LiveData<InfoById> = _filmDetails

    private val _seasons = MutableLiveData<List<SeasonsItem>>()
    val seasons: LiveData<List<SeasonsItem>> = _seasons

    private val _selectedSeason = MutableLiveData<Int>()
    val selectedSeason: LiveData<Int> = _selectedSeason

    private val _errorLiveData = MutableLiveData<Unit?>()
    val errorLiveData: MutableLiveData<Unit?> = _errorLiveData

    var isErrorShown = false

    private fun handleError() {
        _errorLiveData.value = Unit
    }

    fun getSeasons(movieId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getSeasons(movieId)
                _seasons.value = response.items

                if (_selectedSeason.value == null && response.items.isNotEmpty()) {
                    _selectedSeason.value = response.items.first().number
                }
            } catch (_: Exception) {
                handleError()
            }
        }
    }

    fun setSelectedSeason(seasonNumber: Int) {
        _selectedSeason.value = seasonNumber
    }
}