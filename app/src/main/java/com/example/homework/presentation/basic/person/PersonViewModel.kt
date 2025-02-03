package com.example.homework.presentation.basic.person

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework.data.remote.response.InfoById
import com.example.homework.data.remote.response.PersonInfo
import com.example.homework.domain.CinemaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonViewModel @Inject constructor(
    private val repository: CinemaRepository
) : ViewModel() {

    private val _personInfo = MutableLiveData<PersonInfo>()
    val personInfo: LiveData<PersonInfo> = _personInfo

    private val _detailedMovies = MutableLiveData<List<InfoById>>()
    val detailedMovies: LiveData<List<InfoById>> = _detailedMovies

    private val _errorLiveData = MutableLiveData<Unit>()
    val errorLiveData: LiveData<Unit> = _errorLiveData

    var isErrorShown = false

    private fun handleError() {
        _errorLiveData.value = Unit
    }

    fun loadPersonInfo(personId: Int) {
        viewModelScope.launch {
            try {
                val person = repository.getPersonInfo(personId)
                _personInfo.postValue(person)
            } catch (e: Exception) {
                handleError()
            }
        }
    }

    fun loadDetailedMovies(filmIds: List<Int>) {
        viewModelScope.launch {
            try {
                val movies = filmIds.map { repository.getFilmById(it) }
                _detailedMovies.value = movies
            } catch (_: Exception) {
                handleError()
            }
        }
    }
}