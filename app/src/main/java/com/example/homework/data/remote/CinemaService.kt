package com.example.homework.data.remote

import com.example.homework.data.remote.response.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CinemaService {

    @GET("api/v2.2/films/premieres")
    suspend fun getPremieres(
        @Query("year") year: Int,
        @Query("month") month: String
    ): PremiereResponse

    @GET("api/v2.2/films/collections")
    suspend fun getTop250Movies(
        @Query("type") type: String = "TOP_250_MOVIES",
        @Query("page") page: Int
    ): Top250Response

    @GET("api/v2.2/films")
    suspend fun getGeneralList(
        @Query("order") order: String,
        @Query("type") type: String,
        @Query("ratingFrom") ratingFrom: Float,
        @Query("ratingTo") ratingTo: Float,
        @Query("yearFrom") yearFrom: Int,
        @Query("yearTo") yearTo: Int,
        @Query("page") page: Int,
        @Query("countries") countryId: Int? = null,
        @Query("genres") genreId: Int? = null,
        @Query("keyword") keyword: String? = null
    ): GeneralResponse

    @GET("api/v2.2/films/filters")
    suspend fun getFilters(): FiltersResponse

    @GET("api/v2.2/films/{id}")
    suspend fun getFilmById(
        @Path("id") filmId: Int,
    ): InfoById

    @GET("/api/v1/staff")
    suspend fun getTeam(
        @Query("filmId") filmId: Int,
    ): List<TeamItem>

    @GET("/api/v1/staff/{id}")
    suspend fun getPersonInfo(
        @Path("id") personId: Int,
    ): PersonInfo

    @GET("/api/v2.2/films/{id}/images")
    suspend fun getFilmGallery(
        @Path("id") filmId: Int,
        @Query("type") type: String,
        @Query("page") page: Int
    ): GalleryResponse

    @GET("api/v2.2/films/{id}/similars")
    suspend fun getSimilarFilms(
        @Path("id") movieId: Int,
    ): SimilarResponse

    @GET("api/v2.2/films/{id}/seasons")
    suspend fun getSeasons(
        @Path("id") filmId: Int
    ): SeasonsResponse
}
