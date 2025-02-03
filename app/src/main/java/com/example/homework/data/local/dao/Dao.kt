package com.example.homework.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.homework.entity.CategoryEntity
import com.example.homework.entity.MovieCategoryCrossRef
import com.example.homework.entity.MovieEntity

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovie(movie: MovieEntity)

    @Insert
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Delete
    suspend fun deleteMovie(movie: MovieEntity)

    @Query("SELECT * FROM movies WHERE kinopoiskId = :movieId")
    suspend fun getMovieById(movieId: Long): MovieEntity?

    @Query("SELECT * FROM movies WHERE kinopoiskId IN (:movieIds)")
    suspend fun getMoviesByIds(movieIds: List<Int>): List<MovieEntity>

    @Query("DELETE FROM movies WHERE kinopoiskId IN (:movieIds)")
    suspend fun deleteMoviesByIds(movieIds: List<Int>)

    @Query("SELECT * FROM movies")
    suspend fun getAllMovies(): List<MovieEntity>

    @Query("DELETE FROM movies WHERE kinopoiskId = :movieId")
    suspend fun deleteMovieById(movieId: Int)
}

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<CategoryEntity>
}

@Dao
interface MovieCategoryCrossRefDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovieCategoryCrossRef(crossRef: MovieCategoryCrossRef)

    @Delete
    suspend fun deleteMovieCategoryCrossRef(crossRef: MovieCategoryCrossRef)

    @Query("SELECT COUNT(*) > 0 FROM movie_category_cross_ref WHERE movieId = :movieId AND categoryName = :categoryName")
    suspend fun isMovieInCategory(movieId: Int, categoryName: String): Boolean

    @Query("SELECT * FROM movie_category_cross_ref WHERE movieId = :movieId")
    suspend fun getCategoriesForMovie(movieId: Int): List<MovieCategoryCrossRef>

    @Query("SELECT * FROM movie_category_cross_ref WHERE categoryName = :categoryName")
    suspend fun getMoviesForCategory(categoryName: String): List<MovieCategoryCrossRef>

    @Query("DELETE FROM movie_category_cross_ref WHERE categoryName = :categoryName")
    suspend fun deleteAllMoviesInCategory(categoryName: String)

    @Query("DELETE FROM movie_category_cross_ref WHERE movieId = :movieId")
    suspend fun deleteAllCategoriesForMovie(movieId: Int)
}