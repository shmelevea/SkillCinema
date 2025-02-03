package com.example.homework.data.remote.response

data class GalleryResponse(
    val items: List<GalleryItem>,
    val total: Int,
    val totalPages: Int
)

data class GalleryItem(
    val imageUrl: String,
    val previewUrl: String
)
