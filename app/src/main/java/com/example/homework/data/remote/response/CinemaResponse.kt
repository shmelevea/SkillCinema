package com.example.homework.data.remote.response

import com.example.homework.data.remote.dto.MovieItem

data class PremiereResponse(
    val total: Int,
    val items: List<PremiereItem>
)

data class PremiereItem(
    override val kinopoiskId: Int,
    override val nameRu: String,
    val nameEn: String,
    val year: Int,
    val posterUrl: String,
    override val posterUrlPreview: String,
    override val countries: List<Country>,
    override val genres: List<Genre>,
    val duration: Int,
    override val ratingKinopoisk: Double?,
    override val nameOriginal: String,
    val premiereRu: String
) : MovieItem {
    override val categoryName: String = "Premiere"
}

data class Top250Response(
    val items: List<Top250Item>,
    val total: Int,
    val totalPages: Int
)

data class Top250Item(
    override val countries: List<Country>,
    val coverUrl: String,
    val description: String,
    override val genres: List<Genre>,
    val imdbId: String,
    override val kinopoiskId: Int,
    val logoUrl: String,
    val nameEn: Any,
    override val nameOriginal: String,
    override val nameRu: String,
    val posterUrl: String,
    override val posterUrlPreview: String,
    val ratingAgeLimits: String,
    val ratingImdb: Double,
    override val ratingKinopoisk: Double,
    val type: String,
    val year: Int
) : MovieItem {
    override val categoryName: String = "TOP-250"
}

data class GeneralResponse(
    val items: List<GeneralItem>,
    val total: Int,
    val totalPages: Int
)

data class GeneralItem(
    override val countries: List<Country>,
    override val genres: List<Genre>,
    val imdbId: String,
    override val kinopoiskId: Int,
    val nameEn: Any,
    override val nameOriginal: String,
    override val nameRu: String?,
    val posterUrl: String,
    override val posterUrlPreview: String,
    val ratingImdb: Double,
    override val ratingKinopoisk: Double?,
    val type: String,
    val year: Int
) : MovieItem {
    override val categoryName: String = "General"
}

data class FiltersResponse(
    val countries: List<Country>,
    val genres: List<Genre>
)

data class Country(
    val id: Int,
    val country: String,
    val genitive: String
)

data class Genre(
    val id: Int,
    val genre: String,
    val plural: String
)