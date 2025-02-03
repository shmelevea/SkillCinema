package com.example.homework.data.remote

data class SearchFilter(
    val order: String = "RATING",
    val type: String = "ALL",
    val ratingFrom: Float = 0f,
    val ratingTo: Float = 10f,
    val yearFrom: Int = 1950,
    val yearTo: Int = 2024,
    val countryId: Int? = null,
    val genreId: Int? = null
)

data class CountryGenreItem(
    val id: Int,
    val name: String
)