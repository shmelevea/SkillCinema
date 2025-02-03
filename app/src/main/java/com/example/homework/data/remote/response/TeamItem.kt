package com.example.homework.data.remote.response

data class TeamItem(
    val description: String?,
    val nameEn: String,
    val nameRu: String,
    val posterUrl: String,
    val professionKey: String,
    val professionText: String,
    val staffId: Int
)