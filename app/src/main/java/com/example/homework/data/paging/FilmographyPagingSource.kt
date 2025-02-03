package com.example.homework.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.homework.data.remote.response.Film

class FilmographyPagingSource(
    private val allFilms: List<Film>,
    private val loadDetailedMovies: (List<Int>) -> Unit
) : PagingSource<Int, Film>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Film> {
        val start = params.key ?: 0
        val end = minOf(start + params.loadSize, allFilms.size)

        return if (start < allFilms.size) {

            val filmIdsToLoad = allFilms.subList(start, end).map { it.filmId }
            loadDetailedMovies(filmIdsToLoad)

            LoadResult.Page(
                data = allFilms.subList(start, end),
                prevKey = if (start == 0) null else start - params.loadSize,
                nextKey = if (end == allFilms.size) null else end
            )
        } else {
            LoadResult.Error(IllegalArgumentException("Invalid load position"))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Film>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(state.config.pageSize)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(state.config.pageSize)
        }
    }
}