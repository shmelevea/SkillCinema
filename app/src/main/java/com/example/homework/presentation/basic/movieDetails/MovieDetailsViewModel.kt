package com.example.homework.presentation.basic.movieDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework.data.remote.response.*
import com.example.homework.domain.CinemaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val repository: CinemaRepository
) : ViewModel() {

    private val _filmDetails = MutableLiveData<InfoById>()
    val filmDetails: LiveData<InfoById> = _filmDetails

    private val _teamData = MutableLiveData<List<TeamItem>>()
    val teamData: LiveData<List<TeamItem>> = _teamData

    private val _galleryData = MutableLiveData<List<GalleryItem>>()
    val galleryData: LiveData<List<GalleryItem>> = _galleryData

    private val _similarMoviesData = MutableLiveData<List<SimilarItem>>()
    val similarMoviesData: LiveData<List<SimilarItem>> = _similarMoviesData

    private val _detailedSimilarMovies = MutableLiveData<List<InfoById>>()
    val detailedSimilarMovies: LiveData<List<InfoById>> = _detailedSimilarMovies

    private val _seasonsData = MutableLiveData<SeasonsResponse>()
    val seasonsData: LiveData<SeasonsResponse> = _seasonsData

    private val _errorLiveData = MutableLiveData<Unit>()
    val errorLiveData: LiveData<Unit> = _errorLiveData

    private val _totalPhotoCount = MutableLiveData<Int>()
    val totalPhotoCount: LiveData<Int> get() = _totalPhotoCount

    var isErrorShown = false

    private fun handleError() {
        _errorLiveData.value = Unit
    }

    private val actorCount = MutableLiveData<Int>()
    private val teamCount = MutableLiveData<Int>()
    private val similarMoviesCount = MutableLiveData<Int>()

    fun init(movieId: Int) {
        loadFilmDetails(movieId)
        loadTeamData(movieId)
        loadGalleryData(movieId)
        loadSimilarMoviesData(movieId)
    }

    private fun loadFilmDetails(movieId: Int) {
        viewModelScope.launch {
            try {
                val filmDetails = repository.getFilmById(movieId)
                _filmDetails.value = filmDetails
            } catch (_: Exception) {
                handleError()
            }
        }
    }

    private fun loadTeamData(movieId: Int) {
        viewModelScope.launch {
            try {
                val teamItems = repository.getTeam(movieId)
                _teamData.value = teamItems

                val actors = teamItems.filter { it.professionKey == "ACTOR" }
                val nonActors = teamItems.filter { it.professionKey != "ACTOR" }
                actorCount.value = actors.size
                teamCount.value = nonActors.size

            } catch (_: Exception) {
                handleError()
            }
        }
    }

    private fun loadGalleryData(movieId: Int) {
        viewModelScope.launch {
            try {
                val allGalleryItems = mutableListOf<GalleryItem>()

                val totalPhotosCount = getTotalPhotosCount(movieId, imageTypes)
                _totalPhotoCount.value = totalPhotosCount

                run loadImages@{
                    for (type in imageTypes) {
                        var currentPage = 1
                        var moreDataAvailable = true

                        while (moreDataAvailable) {
                            val galleryResponse =
                                repository.getFilmGallery(movieId, type = type, page = currentPage)
                            val itemsToAdd = galleryResponse.items.take(20 - allGalleryItems.size)
                            allGalleryItems.addAll(itemsToAdd)
                            if (allGalleryItems.size >= 20) return@loadImages
                            moreDataAvailable = galleryResponse.items.size >= 20
                            currentPage++
                        }
                    }
                }

                _galleryData.value = allGalleryItems
            } catch (_: Exception) {
                handleError()
            }
        }
    }

    private suspend fun getTotalPhotosCount(movieId: Int, imageTypes: List<String>): Int {
        var totalCount = 0
        for (type in imageTypes) {
            try {
                val galleryResponse = repository.getFilmGallery(movieId, type = type, page = 1)
                totalCount += galleryResponse.total
            } catch (e: Exception) {
                handleError()
            }
        }
        return totalCount
    }

    private fun loadSimilarMoviesData(movieId: Int) {
        viewModelScope.launch {
            try {
                val similarMovies = repository.getSimilarFilms(movieId)
                _similarMoviesData.value = similarMovies
                similarMoviesCount.value = similarMovies.size
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

    fun loadSeasons(movieId: Int) {
        viewModelScope.launch {
            try {
                val seasonsResponse = repository.getSeasons(movieId)
                _seasonsData.value = seasonsResponse
            } catch (e: Exception) {
                handleError()
            }
        }
    }


    companion object {
        val imageTypes = listOf(
            "STILL",
            "SHOOTING",
            "POSTER",
            "FAN_ART",
            "PROMO",
            "CONCEPT",
            "WALLPAPER",
            "COVER",
            "SCREENSHOT"
        )
    }
}