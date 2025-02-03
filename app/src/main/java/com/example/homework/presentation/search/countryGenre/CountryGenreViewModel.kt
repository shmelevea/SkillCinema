package com.example.homework.presentation.search.countryGenre

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework.data.remote.CountryGenreItem
import com.example.homework.data.remote.response.*
import com.example.homework.domain.CinemaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CountryGenreViewModel @Inject constructor(
    private val repository: CinemaRepository
) : ViewModel() {

    private val _countries = MutableLiveData<List<Country>>()
    val countries: LiveData<List<Country>> = _countries

    private val _genres = MutableLiveData<List<Genre>>()
    val genres: LiveData<List<Genre>> = _genres

    private val _filteredItems =
        MutableLiveData<List<CountryGenreItem>>()
    val filteredItems: LiveData<List<CountryGenreItem>> = _filteredItems

    private val _errorLiveData = MutableLiveData<Unit?>()
    val errorLiveData: MutableLiveData<Unit?> = _errorLiveData

    var isErrorShown = false

    private fun handleError() {
        _errorLiveData.value = Unit
    }

    fun loadCountries() {
        viewModelScope.launch {
            try {
                val filters = repository.getFilters()
                _countries.value = filters.countries
                _filteredItems.value = filters.countries.map {
                    CountryGenreItem(it.id, it.country)
                }
            } catch (e: Exception) {
                handleError()
            }
        }
    }

    fun loadGenres() {
        viewModelScope.launch {
            try {
                val filters = repository.getFilters()
                _genres.value = filters.genres
                _filteredItems.value = filters.genres.map {
                    CountryGenreItem(it.id, it.genre.replaceFirstChar { ch ->
                        if (ch.isLowerCase()) ch.titlecase(Locale.ROOT) else ch.toString()
                    })
                }
            } catch (e: Exception) {
                handleError()
            }
        }
    }

    fun filterList(query: String, isCountryRequest: Boolean) {
        val filteredList = when {
            isCountryRequest -> {
                _countries.value?.filter { country ->
                    country.country.contains(query, ignoreCase = true)
                }?.map { CountryGenreItem(it.id, it.country) }
            }

            else -> {
                _genres.value?.filter { genre ->
                    genre.genre.contains(query, ignoreCase = true)
                }?.map {
                    CountryGenreItem(
                        it.id,
                        it.genre.replaceFirstChar { ch ->
                            if (ch.isLowerCase()) ch.titlecase(Locale.ROOT) else ch.toString()
                        }
                    )
                }
            }
        }
        _filteredItems.value = filteredList ?: emptyList()
    }
}