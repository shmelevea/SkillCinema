package com.example.homework.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.homework.data.remote.SearchFilter
import com.example.homework.data.remote.response.GeneralItem
import com.example.homework.domain.CinemaRepository

class SearchPagingSource(
    private val repository: CinemaRepository,
    private val query: String,
    private val filter: SearchFilter
) : PagingSource<Int, GeneralItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GeneralItem> {
        return try {
            val page = params.key ?: 1
            val results = repository.getGeneraList(
                order = filter.order,
                type = filter.type,
                ratingFrom = filter.ratingFrom,
                ratingTo = filter.ratingTo,
                yearFrom = filter.yearFrom,
                yearTo = filter.yearTo,
                countryId = filter.countryId,
                genreId = filter.genreId,
                page = page,
                keyword = query
            )
            LoadResult.Page(
                data = results,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (results.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GeneralItem>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }
}