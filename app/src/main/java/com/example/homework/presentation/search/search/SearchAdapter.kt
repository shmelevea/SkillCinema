package com.example.homework.presentation.search.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.homework.R
import com.example.homework.data.remote.response.GeneralItem
import com.example.homework.databinding.ItemMovieHorizontalBinding
import javax.inject.Inject

class SearchAdapter @Inject constructor(
    private val onItemClick: (GeneralItem) -> Unit
) : PagingDataAdapter<GeneralItem, SearchAdapter.SearchViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding =
            ItemMovieHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class SearchViewHolder(private val binding: ItemMovieHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GeneralItem) {
            val movieTitle = if (!item.nameRu.isNullOrBlank()) item.nameRu else item.nameOriginal
            binding.titleTv.text = movieTitle
            binding.ratingBtn.text = item.ratingKinopoisk.toString()
            Glide.with(binding.root.context)
                .load(item.posterUrlPreview)
                .placeholder(R.color.gray_gag)
                .into(binding.posterIv)

            val rating = item.ratingKinopoisk?.toString()
            binding.ratingBtn.visibility = if (rating.isNullOrEmpty()) View.GONE else View.VISIBLE
            binding.ratingBtn.text = rating

            val year = item.year
            val genres = item.genres.joinToString { it.genre }
            val text = "$year, $genres"
            binding.genreTv.text = text

            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<GeneralItem>() {
            override fun areItemsTheSame(oldItem: GeneralItem, newItem: GeneralItem): Boolean =
                oldItem.kinopoiskId == newItem.kinopoiskId

            override fun areContentsTheSame(oldItem: GeneralItem, newItem: GeneralItem): Boolean =
                oldItem == newItem
        }
    }
}