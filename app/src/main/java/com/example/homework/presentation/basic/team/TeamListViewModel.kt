package com.example.homework.presentation.basic.team

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework.data.remote.response.TeamItem
import com.example.homework.domain.CinemaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamListViewModel @Inject constructor(
    private val repository: CinemaRepository
) : ViewModel() {

    private val _teamData = MutableLiveData<List<TeamItem>>()
    val teamData: LiveData<List<TeamItem>> = _teamData

    private val _errorLiveData = MutableLiveData<Unit?>()
    val errorLiveData: MutableLiveData<Unit?> = _errorLiveData

    var isErrorShown = false

    private fun handleError() {
        _errorLiveData.value = Unit
    }

    fun loadTeamData(movieId: Int, isActorsList: Boolean) {
        viewModelScope.launch {
            try {
                val teamItems = repository.getTeam(movieId)
                val filteredList = teamItems.filter {
                    if (isActorsList) it.professionKey == "ACTOR" else it.professionKey != "ACTOR"
                }
                _teamData.value = filteredList
            } catch (_: Exception) {
                handleError()
            }
        }
    }
}