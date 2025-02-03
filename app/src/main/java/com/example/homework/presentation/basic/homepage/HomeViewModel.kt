package com.example.homework.presentation.basic.homepage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework.data.MovieCategory
import com.example.homework.data.remote.dto.RandomContent
import com.example.homework.data.remote.response.*
import com.example.homework.domain.CinemaRepository
import com.example.homework.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cinemaRepository: CinemaRepository
) : ViewModel() {

    private val _premieresLiveData = MutableLiveData<List<PremiereItem>>()
    val premieresLiveData: LiveData<List<PremiereItem>> = _premieresLiveData

    private val _top250LiveData = MutableLiveData<List<Top250Item>>()
    val top250LiveData: LiveData<List<Top250Item>> = _top250LiveData

    private val _firstRandomMoviesLiveData = MutableLiveData<RandomContent>()
    val firstRandomMoviesLiveData: LiveData<RandomContent> = _firstRandomMoviesLiveData

    private val _secondRandomMoviesLiveData = MutableLiveData<RandomContent>()
    val secondRandomMoviesLiveData: LiveData<RandomContent> = _secondRandomMoviesLiveData

    private val _tvSeriesLiveData = MutableLiveData<RandomContent>()
    val tvSeriesLiveData: LiveData<RandomContent> = _tvSeriesLiveData

    private val _errorLiveData = MutableLiveData<Unit?>()
    val errorLiveData: MutableLiveData<Unit?> = _errorLiveData

    private val _isRetryButtonVisible = MutableLiveData(false)
    val isRetryButtonVisible: LiveData<Boolean> = _isRetryButtonVisible

    var isErrorShown = false

    private fun handleError() {
        _errorLiveData.value = Unit
        _isRetryButtonVisible.value = true
    }

    val combinedLiveData = MediatorLiveData<Unit>().apply {
        addSource(premieresLiveData) { value = Unit }
        addSource(top250LiveData) { value = Unit }
        addSource(firstRandomMoviesLiveData) { value = Unit }
        addSource(secondRandomMoviesLiveData) { value = Unit }
        addSource(tvSeriesLiveData) { value = Unit }
    }

    fun fetchPremieres() {
        viewModelScope.launch {
            try {
                val premieres = cinemaRepository.getPremieres()
                _premieresLiveData.value = premieres.take(20)
                _isRetryButtonVisible.value = false
            } catch (e: Exception) {
                handleError()
            }
        }
    }

    fun fetchTop250() {
        viewModelScope.launch {
            try {
                val top250Movies = cinemaRepository.getTop250Movies(1)
                _top250LiveData.value = top250Movies
                _isRetryButtonVisible.value = false
            } catch (e: Exception) {
                handleError()
            }
        }
    }

    fun fetchFirstRandomMovies() {
        fetchRandomContent(FILM, _firstRandomMoviesLiveData)
    }

    fun fetchSecondRandomMovies() {
        fetchRandomContent(FILM, _secondRandomMoviesLiveData)
    }

    fun fetchTVSeries() {
        fetchRandomContent(TV_SERIES, _tvSeriesLiveData)
    }

    private fun fetchRandomContent(
        type: String,
        contentLiveData: MutableLiveData<RandomContent>
    ) {
        viewModelScope.launch {
            try {
                val filters = cinemaRepository.getFilters()
                val randomCountry = filters.countries.random()
                val randomGenre = filters.genres.random()

                val movies = cinemaRepository.getGeneraList(
                    type = type,
                    order = YEAR,
                    ratingFrom = 0f,
                    ratingTo = 10f,
                    yearFrom = 1950,
                    yearTo = 2024,
                    page = 1,
                    countryId = randomCountry.id,
                    genreId = randomGenre.id,
                    keyword = null
                )

                if (movies.isEmpty()) {
                    fetchRandomContent(type, contentLiveData)
                } else {
                    contentLiveData.value = RandomContent(movies, randomCountry.id, randomGenre.id)
                }
                _isRetryButtonVisible.value = false
            } catch (e: Exception) {
                handleError()
            }
        }
    }

    fun isMovieViewed(movieId: Int): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            result.postValue(cinemaRepository.isMovieInCategory(movieId, MovieCategory.Viewed.name))
        }
        return result
    }
}