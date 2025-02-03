package com.example.homework.presentation.basic.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.example.homework.data.paging.GalleryPagingSource
import com.example.homework.data.remote.response.GalleryItem
import com.example.homework.domain.CinemaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val repository: CinemaRepository
) : ViewModel() {

    private val imageTypes = listOf(
        "POSTER", "STILL", "SHOOTING", "FAN_ART", "PROMO",
        "CONCEPT", "WALLPAPER", "COVER", "SCREENSHOT"
    )

    private val _availableCategories = MutableLiveData<List<Pair<String, Int>>>()
    val availableCategories: LiveData<List<Pair<String, Int>>> get() = _availableCategories

    private val _selectedCategory = MutableLiveData<String>()
    val selectedCategory: LiveData<String> get() = _selectedCategory

    private val _errorLiveData = MutableLiveData<Unit?>()
    val errorLiveData: MutableLiveData<Unit?> = _errorLiveData

    var isErrorShown = false

    private fun handleError() {
        _errorLiveData.value = Unit
    }

    fun getPhotosByCategory(movieId: Int, category: String): LiveData<PagingData<GalleryItem>> {
        return Pager(PagingConfig(pageSize = 20)) {
            GalleryPagingSource(repository, movieId, category)
        }.liveData.cachedIn(viewModelScope)
    }

    fun loadAvailableCategories(movieId: Int) {
        viewModelScope.launch {
            try {
                val categoriesWithCounts = imageTypes.mapNotNull { category ->
                    try {
                        val response = repository.getFilmGallery(movieId, category, 1)
                        val count = response.total
                        if (count > 0) category to count else null
                    } catch (e: Exception) {
                        handleError()
                        null
                    }
                }
                _availableCategories.value = categoriesWithCounts
                if (categoriesWithCounts.isNotEmpty() && _selectedCategory.value == null) {
                    _selectedCategory.value = categoriesWithCounts.first().first
                }
            } catch (e: Exception) {
                handleError()
            }
        }
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }
}