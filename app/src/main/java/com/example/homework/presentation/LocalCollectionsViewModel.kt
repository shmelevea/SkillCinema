package com.example.homework.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework.data.MovieCategory
import com.example.homework.data.mapper.toMovieEntity
import com.example.homework.data.remote.response.InfoById
import com.example.homework.domain.CinemaRepository
import com.example.homework.entity.CategoryItem
import com.example.homework.entity.MovieEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalCollectionsViewModel @Inject constructor(
    private val repository: CinemaRepository
) : ViewModel() {

    private val _interestedMovies = MutableLiveData<List<MovieEntity>>()
    val interestedMovies: LiveData<List<MovieEntity>> get() = _interestedMovies

    private val _viewedMovies = MutableLiveData<List<MovieEntity>>()
    val viewedMovies: LiveData<List<MovieEntity>> get() = _viewedMovies

    private val _detailedMovies = MutableLiveData<List<InfoById>>()
    val detailedMovies: LiveData<List<InfoById>> get() = _detailedMovies

    private val _categoryData = MutableLiveData<List<CategoryItem>>()
    val categoryData: LiveData<List<CategoryItem>> = _categoryData

    private val _interestedMoviesCount = MutableLiveData<Int>()
    val interestedMoviesCount: LiveData<Int> get() = _interestedMoviesCount

    private val _viewedMoviesCount = MutableLiveData<Int>()
    val viewedMoviesCount: LiveData<Int> get() = _viewedMoviesCount


    fun init(movieId: Int) {
        addInterested(movieId)
        observeMovieCategoryStatus(movieId)
    }

    private val categoryStatuses = mutableMapOf<MovieCategory, MutableLiveData<Boolean>>(
        MovieCategory.Viewed to MutableLiveData(false),
        MovieCategory.Liked to MutableLiveData(false),
        MovieCategory.Wishlist to MutableLiveData(false)
    )

    private val _errorLiveData = MutableLiveData<Unit>()
    val errorLiveData: LiveData<Unit> = _errorLiveData

    private fun handleError() {
        _errorLiveData.value = Unit
    }

    var isErrorShown = false

    private fun addInterested(movieId: Int) {
        viewModelScope.launch {
            try {
                val filmDetails = repository.getFilmById(movieId)
                val isInCategory =
                    repository.isMovieInCategory(movieId, MovieCategory.Interested.name)
                if (!isInCategory) {
                    toggleMovieInCategory(filmDetails, MovieCategory.Interested){
                    }
                }
            } catch (_: Exception) {
                handleError()
            }
        }
    }

    fun isMovieInCategory(movieId: Int, category: MovieCategory): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val isInCategory = repository.isMovieInCategorySync(movieId, category.name)
            result.postValue(isInCategory)
        }
        return result
    }

    private fun checkIfMovieInCategory(movieId: Int, category: MovieCategory) {
        viewModelScope.launch {
            try {
                if (category is MovieCategory.Custom) {
                    val categoryExists = repository.isCategoryExists(category.name)
                    if (categoryExists) {
                        val isInCategory = repository.isMovieInCategory(movieId, category.name)
                        categoryStatuses[category]?.postValue(isInCategory)
                    } else {
                        categoryStatuses[category]?.postValue(false)
                    }
                } else {
                    val isInCategory = repository.isMovieInCategory(movieId, category.name)
                    categoryStatuses[category]?.postValue(isInCategory)
                }
            } catch (e: Exception) {
                handleError()
            }
        }
    }

    fun observeMovieCategoryStatus(movieId: Int) {
        listOf(
            MovieCategory.Viewed,
            MovieCategory.Liked,
            MovieCategory.Wishlist
        ).forEach { category ->
            checkIfMovieInCategory(movieId, category)
        }
    }

    fun loadMovies() {
        viewModelScope.launch {
            try {
                val interested = repository.getMoviesByCategory(MovieCategory.Interested)
                _interestedMovies.postValue(interested)
                _interestedMoviesCount.postValue(interested.size)

                val viewed = repository.getMoviesByCategory(MovieCategory.Viewed)
                _viewedMovies.postValue(viewed)
                _viewedMoviesCount.postValue(viewed.size)

                val movieIds = (interested + viewed).map { it.kinopoiskId }.distinct()
                val detailed = repository.loadDetailedMovies(movieIds)
                _detailedMovies.postValue(detailed)
            } catch (_: Exception) {
                handleError()
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            try {
                val categories = repository.getAllCategories()
                val categoryItems = mutableListOf<CategoryItem>()
                for (category in categories) {
                    val count = repository.getMoviesCountInCategory(category)
                    categoryItems.add(CategoryItem(category, count))
                }
                _categoryData.value = categoryItems
            } catch (_: Exception) {
                handleError()
            }
        }
    }

    fun removeCategory(categoryItem: CategoryItem) {
        viewModelScope.launch {
            try {
                repository.deleteMoviesInCategory(categoryItem.category)
                repository.deleteCategory(categoryItem.category)
                loadCategories()
                loadMovies()
            } catch (_: Exception) {
                handleError()
            }
        }
    }

    fun createCategory(categoryName: String) {
        viewModelScope.launch {
            try {
                if (categoryName.isBlank() || categoryName.length > 30) {
                    return@launch
                }
                repository.addCustomCategory(categoryName)
            } catch (_: Exception) {
                handleError()
            } finally {
                loadCategories()
            }
        }
    }

    fun toggleMovieInCategory(movie: InfoById, category: MovieCategory, onCompleted: () -> Unit) {
        viewModelScope.launch {
            try {
                if (category is MovieCategory.Custom) {
                    val categoryExists = repository.isCategoryExists(category.name)
                    if (!categoryExists) {
                        handleError()
                        return@launch
                    }
                }

                val isInCategory = repository.isMovieInCategory(movie.kinopoiskId, category.name)
                if (isInCategory) {
                    repository.removeMovieFromCategory(movie.kinopoiskId, category.name)
                    categoryStatuses[category]?.postValue(false)
                } else {
                    val movieEntity = movie.toMovieEntity()
                    repository.addMovieToCategory(movieEntity, category.name)
                    categoryStatuses[category]?.postValue(true)
                }

                loadCategories()
                onCompleted()
            } catch (e: Exception) {
                handleError()
            }
        }
    }
}