package com.example.homework.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_category_cross_ref")
data class MovieCategoryCrossRef(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val movieId: Int,
    val categoryName: String
)