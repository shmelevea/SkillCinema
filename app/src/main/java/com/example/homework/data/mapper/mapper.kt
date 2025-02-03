package com.example.homework.data.mapper

import com.example.homework.data.remote.dto.MovieItem
import com.example.homework.data.remote.response.Country
import com.example.homework.data.remote.response.Genre
import com.example.homework.data.remote.response.InfoById
import com.example.homework.entity.MovieEntity

fun InfoById.toMovieEntity(): MovieEntity {
    return MovieEntity(
        kinopoiskId = this.kinopoiskId,
        nameRu = this.nameRu,
        posterUrlPreview = this.posterUrlPreview,
        genres = this.genres.joinToString(",") { it.genre },
        countries = this.countries.joinToString(",") { it.country },
        ratingKinopoisk = this.ratingKinopoisk,
        nameOriginal = this.nameOriginal
    )
}

fun InfoById.toMovieItem(categoryName: String): MovieItem {
    return object : MovieItem {
        override val nameRu: String? = this@toMovieItem.nameRu
        override val posterUrlPreview: String = this@toMovieItem.posterUrlPreview
        override val genres: List<Genre> = this@toMovieItem.genres
        override val kinopoiskId: Int = this@toMovieItem.kinopoiskId
        override val categoryName: String = categoryName
        override val ratingKinopoisk: Double? = this@toMovieItem.ratingKinopoisk
        override val countries: List<Country> = this@toMovieItem.countries
        override val nameOriginal: String = this@toMovieItem.nameOriginal
    }
}
