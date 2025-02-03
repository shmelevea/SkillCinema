package com.example.homework.presentation.search.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.homework.data.remote.SearchFilter
import com.example.homework.data.paging.SearchPagingSource
import com.example.homework.data.remote.response.GeneralItem
import com.example.homework.domain.CinemaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: CinemaRepository
) : ViewModel() {

    private val _searchFilter = MutableStateFlow(SearchFilter())
    private val searchFilter: StateFlow<SearchFilter> = _searchFilter

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> get() = _searchQuery

    private val _errorLiveData = MutableLiveData<Unit>()
    val errorLiveData: MutableLiveData<Unit> = _errorLiveData

    var isErrorShown = false

    private fun handleError() {
        _errorLiveData.value = Unit
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateFilter(newFilter: SearchFilter) {
        _searchFilter.value = newFilter
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun searchMovies(query: String): Flow<PagingData<GeneralItem>> {
        return searchFilter.flatMapLatest { filter ->
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    SearchPagingSource(repository, query, filter)
                }
            ).flow.cachedIn(viewModelScope).onEach {
            }.catch {
                handleError()
            }
        }
    }
}