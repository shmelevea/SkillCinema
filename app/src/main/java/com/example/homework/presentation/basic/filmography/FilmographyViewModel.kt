package com.example.homework.presentation.basic.filmography

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.homework.data.paging.FilmographyPagingSource
import com.example.homework.data.remote.response.Film
import com.example.homework.data.remote.response.InfoById
import com.example.homework.data.remote.response.PersonInfo
import com.example.homework.domain.CinemaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilmographyViewModel @Inject constructor(
    private val repository: CinemaRepository
) : ViewModel() {

    private val _filmography = MutableLiveData<PersonInfo>()
    val filmography: LiveData<PersonInfo> get() = _filmography

    private val _filmsByProfession = MutableLiveData<Map<String, Pair<List<Film>, Int>>>()
    val filmsByProfession: LiveData<Map<String, Pair<List<Film>, Int>>> get() = _filmsByProfession

    private val _selectedCategory = MutableLiveData<String>()
    val selectedCategory: LiveData<String> get() = _selectedCategory

    private val _detailedMovies = MutableLiveData<List<InfoById>>()
    val detailedMovies: LiveData<List<InfoById>> = _detailedMovies

    private val _errorLiveData = MutableLiveData<Unit?>()
    val errorLiveData: MutableLiveData<Unit?> = _errorLiveData

    var isErrorShown = false

    private fun handleError() {
        _errorLiveData.value = Unit
    }

    private val _filteredFilms = MediatorLiveData<List<Film>>().apply {
        addSource(_selectedCategory) { updateFilteredFilms() }
        addSource(_filmsByProfession) { updateFilteredFilms() }
    }

    init {
        _selectedCategory.value = ""
    }

    private fun updateFilteredFilms() {
        _filteredFilms.value =
            _filmsByProfession.value?.get(_selectedCategory.value)?.first.orEmpty()
    }

    fun loadFilmography(personId: Int) {
        viewModelScope.launch {
            try {
                val person = repository.getPersonInfo(personId)
                _filmography.postValue(person)

                val filmsByCategory = person.films
                    .groupBy { it.professionKey }
                    .mapValues { (_, films) -> films to films.size }

                _filmsByProfession.postValue(filmsByCategory)

                if (_selectedCategory.value.isNullOrEmpty()) {
                    _selectedCategory.postValue(filmsByCategory.keys.firstOrNull())
                }
            } catch (e: Exception) {
                handleError()
            }
        }
    }

    val pagedFilms: LiveData<PagingData<Film>> = _filteredFilms.switchMap { films ->
        Pager(
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 10,
                prefetchDistance = 1
            ),
            pagingSourceFactory = { FilmographyPagingSource(films) { loadDetailedMovies(it) } }
        ).flow.cachedIn(viewModelScope).asLiveData()
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    private fun loadDetailedMovies(filmIds: List<Int>) {
        viewModelScope.launch {
            try {
                filmIds.forEach { filmId ->
                    val detailedMovie = repository.getFilmById(filmId)
                    _detailedMovies.value =
                        _detailedMovies.value?.plus(detailedMovie) ?: listOf(detailedMovie)
                }
            } catch (_: Exception) {
                handleError()
            }
        }
    }
}