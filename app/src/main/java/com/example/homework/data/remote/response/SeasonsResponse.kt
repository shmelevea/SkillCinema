package com.example.homework.data.remote.response

data class SeasonsResponse(
    val items: List<SeasonsItem>,
    val total: Int
)

data class SeasonsItem(
    val episodes: List<Episode>,
    val number: Int
)

data class Episode(
    val episodeNumber: Int,
    val nameEn: String?,
    val nameRu: String?,
    val releaseDate: String?,
    val seasonNumber: Int,
    val synopsis: Any
)

