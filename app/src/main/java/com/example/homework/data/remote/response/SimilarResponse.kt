package com.example.homework.data.remote.response

data class SimilarResponse(
    val items: List<SimilarItem>,
    val total: Int
)

data class SimilarItem(
    val filmId: Int,
    val nameEn: String,
    val nameOriginal: String,
    val nameRu: String,
    val posterUrl: String,
    val posterUrlPreview: String,
    val relationType: String,
)
