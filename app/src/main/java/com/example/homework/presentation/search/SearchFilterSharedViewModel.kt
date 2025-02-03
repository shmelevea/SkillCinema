package com.example.homework.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.homework.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SearchFilterSharedViewModel @Inject constructor(
) : ViewModel() {

    private val _typeMovie = MutableStateFlow(ALL)
    val typeMovie: StateFlow<String> = _typeMovie

    private val _sortOrder = MutableStateFlow(YEAR)
    val sortOrder: StateFlow<String> = _sortOrder

    private val _ratingRange = MutableStateFlow(0f to 10f)
    val ratingRange: StateFlow<Pair<Float, Float>> = _ratingRange

    private val _selectedCountry = MutableStateFlow<String?>(null)
    val selectedCountry: StateFlow<String?> = _selectedCountry

    private val _selectedGenre = MutableStateFlow<String?>(null)
    val selectedGenre: StateFlow<String?> = _selectedGenre

    private val _selectedCountryId = MutableLiveData<Int>()
    val selectedCountryId: LiveData<Int> = _selectedCountryId

    private val _selectedGenreId = MutableLiveData<Int>()
    val selectedGenreId: LiveData<Int> = _selectedGenreId

    private val _yearRange = MutableStateFlow(1950 to LocalDate.now().year)
    val yearRange: StateFlow<Pair<Int, Int>> = _yearRange

    fun setMovieType(type: String) {
        _typeMovie.value = type
    }

    fun setSortOrder(order: String) {
        _sortOrder.value = order
    }

    fun setRatingRange(from: Float, to: Float) {
        _ratingRange.value = from to to
    }

    fun setSelectedCountry(country: String?) {
        _selectedCountry.value = country
    }

    fun setSelectedGenre(genre: String?) {
        _selectedGenre.value = genre
    }

    fun setYearRange(from: Int, to: Int) {
        _yearRange.value = from to to
    }

    fun setSelectedCountryId(id: Int) {
        _selectedCountryId.value = id
    }

    fun setSelectedGenreId(id: Int) {
        _selectedGenreId.value = id
    }
}