package com.example.homework.presentation.basic.similar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework.data.remote.response.InfoById
import com.example.homework.data.remote.response.SimilarItem
import com.example.homework.domain.CinemaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SimilarListViewModel @Inject constructor(
    private val repository: CinemaRepository
) : ViewModel() {

    private val _similarMovies = MutableLiveData<List<SimilarItem>>()
    val similarMovies: LiveData<List<SimilarItem>> get() = _similarMovies

    private val _detailedSimilarMovies = MutableLiveData<List<InfoById>>()
    val detailedSimilarMovies: LiveData<List<InfoById>> = _detailedSimilarMovies

    private val _errorLiveData = MutableLiveData<Unit?>()
    val errorLiveData: MutableLiveData<Unit?> = _errorLiveData

    var isErrorShown = false

    private fun handleError() {
        _errorLiveData.value = Unit
    }

    fun loadSimilarMoviesData(movieId: Int) {
        viewModelScope.launch {
            try {
                val similarMovies = repository.getSimilarFilms(movieId)
                _similarMovies.value = similarMovies
            } catch (_: Exception) {
                handleError()
            }
        }
    }

    fun loadDetailedSimilarMovies(similarMovies: List<SimilarItem>) {
        viewModelScope.launch {
            val detailedMovies = mutableListOf<InfoById>()

            similarMovies.forEach { movie ->
                try {
                    val detailedMovie = repository.getFilmById(movie.filmId)
                    detailedMovies.add(detailedMovie)
                } catch (_: Exception) {
                    handleError()
                }
            }
            _detailedSimilarMovies.value = detailedMovies
        }
    }
}