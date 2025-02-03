package com.example.homework.data.remote.dto

import com.example.homework.data.remote.response.Country
import com.example.homework.data.remote.response.GeneralItem
import com.example.homework.data.remote.response.Genre

interface MovieItem {
    val nameRu: String?
    val posterUrlPreview: String
    val genres: List<Genre>
    val kinopoiskId: Int
    val categoryName: String
    val ratingKinopoisk: Double?
    val countries: List<Country>
    val nameOriginal: String
}

data class Category(
    val categoryName: String,
    val movies: List<MovieItem>,
    val countryId: Int?,
    val genreId: Int?
)

data class RandomContent(
    val movies: List<GeneralItem>,
    val countryId: Int,
    val genreId: Int
)

