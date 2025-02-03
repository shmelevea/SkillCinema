package com.example.homework.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.homework.data.remote.response.GalleryItem
import com.example.homework.domain.CinemaRepository

class GalleryPagingSource(
    private val repository: CinemaRepository,
    private val movieId: Int,
    private val category: String
) : PagingSource<Int, GalleryItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryItem> {
        val page = params.key ?: 1
        return try {
            val response = repository.getFilmGallery(movieId, category, page)
            val nextKey = if (page >= response.totalPages) null else page + 1

            LoadResult.Page(
                data = response.items,
                prevKey = if (page == 1) null else page - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GalleryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}