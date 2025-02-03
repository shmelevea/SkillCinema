package com.example.homework.data

import com.example.homework.entity.CategoryEntity

sealed class MovieCategory(val name: String) {
    data object Viewed : MovieCategory("viewed")
    data object Liked : MovieCategory("liked")
    data object Wishlist : MovieCategory("wishlist")
    data object Interested : MovieCategory("interested")
    data class Custom(val customName: String) : MovieCategory(customName)
}

fun CategoryEntity.toMovieCategory(): MovieCategory {
    return MovieCategory.Custom(this.name)
}