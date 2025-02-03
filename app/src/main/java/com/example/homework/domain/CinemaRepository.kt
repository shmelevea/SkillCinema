package com.example.homework.domain

import com.example.homework.data.MovieCategory
import com.example.homework.data.local.dao.*
import com.example.homework.utils.MonthConverter
import com.example.homework.data.remote.*
import com.example.homework.data.remote.response.*
import com.example.homework.entity.CategoryEntity
import com.example.homework.entity.MovieCategoryCrossRef
import com.example.homework.entity.MovieEntity
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject

class CinemaRepository @Inject constructor(
    private val cinemaService: CinemaService,
    private val movieDao: MovieDao,
    private val categoryDao: CategoryDao,
    private val movieCategoryCrossRefDao: MovieCategoryCrossRefDao
) {

    suspend fun getPremieres(): List<PremiereItem> {
        val today = LocalDate.now()
        val twoWeeksLater = today.plusWeeks(2)

        val currentMonth = MonthConverter.monthToString(today.monthValue)
        val nextMonth = MonthConverter.monthToString(twoWeeksLater.monthValue)

        val premieresCurrentMonth =
            cinemaService.getPremieres(year = today.year, month = currentMonth).items
        val premieresNextMonth =
            cinemaService.getPremieres(year = twoWeeksLater.year, month = nextMonth).items

        val premieres = mutableListOf<PremiereItem>()
        premieres.addAll(premieresCurrentMonth)
        premieres.addAll(premieresNextMonth)

        val filteredPremieres = premieres.filter { premiere ->
            try {
                val premiereDate = LocalDate.parse(premiere.premiereRu)
                premiereDate in today..twoWeeksLater
            } catch (e: DateTimeParseException) {
                false
            }
        }
        return filteredPremieres
    }

    suspend fun getTop250Movies(page: Int): List<Top250Item> {
        val response = cinemaService.getTop250Movies(page = page)
        return response.items
    }

    suspend fun getGeneraList(
        order: String,
        type: String,
        ratingFrom: Float,
        ratingTo: Float,
        yearFrom: Int,
        yearTo: Int,
        page: Int,
        countryId: Int? = null,
        genreId: Int? = null,
        keyword: String? = null
    ): List<GeneralItem> {
        val response = cinemaService.getGeneralList(
            order = order,
            type = type,
            ratingFrom = ratingFrom,
            ratingTo = ratingTo,
            yearFrom = yearFrom,
            yearTo = yearTo,
            page = page,
            countryId = countryId,
            genreId = genreId,
            keyword = keyword
        )
        return response.items
    }

    suspend fun getFilters(): FiltersResponse {
        return cinemaService.getFilters()
    }

    suspend fun getFilmById(movieId: Int): InfoById {
        return cinemaService.getFilmById(filmId = movieId)
    }

    suspend fun getTeam(movieId: Int): List<TeamItem> {
        return cinemaService.getTeam(filmId = movieId)
    }

    suspend fun getFilmGallery(movieId: Int, type: String, page: Int): GalleryResponse {
        return cinemaService.getFilmGallery(filmId = movieId, type = type, page = page)
    }

    suspend fun getSimilarFilms(movieId: Int): List<SimilarItem> {
        val response = cinemaService.getSimilarFilms(movieId = movieId)
        return response.items
    }

    suspend fun getPersonInfo(personId: Int): PersonInfo {
        return cinemaService.getPersonInfo(personId = personId)
    }

    suspend fun getSeasons(movieId: Int): SeasonsResponse {
        return cinemaService.getSeasons(filmId = movieId)
    }

    suspend fun getMoviesCountInCategory(category: CategoryEntity): Int {
        val crossRefs = movieCategoryCrossRefDao.getMoviesForCategory(category.name)
        return crossRefs.size
    }

    suspend fun getAllCategories(): List<CategoryEntity> {
        return categoryDao.getAllCategories()
    }

    suspend fun isMovieInCategory(movieId: Int, categoryName: String): Boolean {
        return movieCategoryCrossRefDao.getCategoriesForMovie(movieId)
            .any { it.categoryName == categoryName }
    }

    suspend fun addMovieToCategory(movie: MovieEntity, categoryName: String) {
        movieDao.insertMovie(movie)
        categoryDao.insertCategory(CategoryEntity(categoryName))
        val crossRef = MovieCategoryCrossRef(movieId = movie.kinopoiskId, categoryName = categoryName)
        movieCategoryCrossRefDao.insertMovieCategoryCrossRef(crossRef)
    }

    suspend fun removeMovieFromCategory(movieId: Int, categoryName: String) {
        val existingCrossRefs = movieCategoryCrossRefDao.getCategoriesForMovie(movieId)
            .filter { it.categoryName == categoryName }

        if (existingCrossRefs.isNotEmpty()) {
            val crossRef = existingCrossRefs.first()
            movieCategoryCrossRefDao.deleteMovieCategoryCrossRef(crossRef)
        }
    }

    suspend fun getMoviesByCategory(category: MovieCategory): List<MovieEntity> {
        val categoryName = category.name
        val crossRefs = movieCategoryCrossRefDao.getMoviesForCategory(categoryName)
        val movieIds = crossRefs.map { it.movieId }
        return movieDao.getMoviesByIds(movieIds)
    }

    suspend fun loadDetailedMovies(movieIds: List<Int>): List<InfoById> {
        return movieIds.mapNotNull { movieId ->
            try {
                cinemaService.getFilmById(movieId)
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun getMovieIdsByCategory(categoryName: String): List<Int> {
        val crossRefs = movieCategoryCrossRefDao.getMoviesForCategory(categoryName)
        return crossRefs.map { it.movieId }
    }

    suspend fun loadMoviesByIds(movieIds: List<Int>): List<InfoById> {
        return movieIds.mapNotNull { movieId ->
            try {
                cinemaService.getFilmById(movieId)
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun deleteMoviesInCategory(category: CategoryEntity) {
        val crossRefs = movieCategoryCrossRefDao.getMoviesForCategory(category.name)
        val movieIds = crossRefs.map { it.movieId }

        movieCategoryCrossRefDao.deleteAllMoviesInCategory(category.name)
        for (movieId in movieIds) {
            val categoriesForMovie = movieCategoryCrossRefDao.getCategoriesForMovie(movieId)
            if (categoriesForMovie.isEmpty()) {
                    movieDao.deleteMovieById(movieId)
            }
        }
    }

    suspend fun deleteCategory(category: CategoryEntity) {
        deleteMoviesInCategory(category)
        categoryDao.deleteCategory(category)
        deleteUncategorizedMovies()
    }

    private suspend fun deleteUncategorizedMovies() {
        val allMovies = movieDao.getAllMovies()
        for (movie in allMovies) {
            val categoriesForMovie = movieCategoryCrossRefDao.getCategoriesForMovie(movie.kinopoiskId)
            if (categoriesForMovie.isEmpty()) {
                movieDao.deleteMovie(movie)
            }
        }
    }

    suspend fun addCustomCategory(categoryName: String) {
        val customCategoryEntity = CategoryEntity(categoryName)
        categoryDao.insertCategory(customCategoryEntity)
    }

    suspend fun isCategoryExists(categoryName: String): Boolean {
        val categories = categoryDao.getAllCategories()
        return categories.any { it.name == categoryName }
    }

    suspend fun isMovieInCategorySync(movieId: Int, categoryName: String): Boolean {
        return movieCategoryCrossRefDao.isMovieInCategory(movieId, categoryName)
    }
}