package com.example.homework.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val kinopoiskId: Int,
    val nameRu: String?,
    val posterUrlPreview: String?,
    val genres: String,
    val countries: String,
    val ratingKinopoisk: Double?,
    val nameOriginal: String?
)