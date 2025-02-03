package com.example.homework.presentation.basic.listOfMovies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework.data.mapper.toMovieItem
import com.example.homework.data.remote.dto.MovieItem
import com.example.homework.domain.CinemaRepository
import com.example.homework.utils.PREMIERE
import com.example.homework.utils.TOP_250
import com.example.homework.utils.YEAR
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListOfMoviesViewModel @Inject constructor(
    private val cinemaRepository: CinemaRepository
) : ViewModel() {

    private val _moviesLiveData = MutableLiveData<List<MovieItem>>()
    val moviesLiveData: LiveData<List<MovieItem>> = _moviesLiveData

    private val _errorLiveData = MutableLiveData<Unit?>()
    val errorLiveData: MutableLiveData<Unit?> = _errorLiveData

    var isErrorShown = false

    private fun handleError() {
        _errorLiveData.value = Unit
    }

    var pageSize = 20
    private var currentPage = 1
    var isLoading = false
    var isLastPage = false

    fun fetchMovies(type: String, countryId: Int?, genreId: Int?) {
        viewModelScope.launch {
            loadNextPage(type, countryId, genreId)
        }
    }

    fun loadNextPage(type: String, countryId: Int?, genreId: Int?) {
        if (isLoading) return

        isLoading = true
        viewModelScope.launch {
            try {
                val content = when (type) {
                    PREMIERE -> {
                        try {
                            val premieres = cinemaRepository.getPremieres()
                            isLastPage = true
                            premieres
                        } catch (e: Exception) {
                            handleError()
                            emptyList()
                        }
                    }

                    TOP_250 -> {
                        try {
                            cinemaRepository.getTop250Movies(currentPage)
                        } catch (e: Exception) {
                            handleError()
                            emptyList()
                        }
                    }

                    else -> {
                        try {
                            cinemaRepository.getGeneraList(
                                type = type,
                                order = YEAR,
                                ratingFrom = 0f,
                                ratingTo = 10f,
                                yearFrom = 1950,
                                yearTo = 2024,
                                page = currentPage,
                                countryId = countryId,
                                genreId = genreId
                            )
                        } catch (e: Exception) {
                            handleError()
                            emptyList()
                        }
                    }
                }

                if (content.isNotEmpty()) {
                    val currentList = _moviesLiveData.value?.toMutableList() ?: mutableListOf()
                    currentList.addAll(content)
                    _moviesLiveData.value = currentList
                    currentPage++

                    if (content.size < pageSize) {
                        isLastPage = true
                    }
                } else {
                    isLastPage = true
                }
            } catch (e: Exception) {
                handleError()
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchMoviesByCategory(categoryName: String) {
        viewModelScope.launch {
            try {
                val movieIds = cinemaRepository.getMovieIdsByCategory(categoryName)
                val movies = cinemaRepository.loadMoviesByIds(movieIds)
                val movieItems = movies.map { it.toMovieItem(categoryName) }
                _moviesLiveData.postValue(movieItems)
            } catch (e: Exception) {
                handleError()
            }
        }
    }
}