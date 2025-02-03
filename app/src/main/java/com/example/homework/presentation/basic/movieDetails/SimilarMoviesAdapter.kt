package com.example.homework.presentation.basic.movieDetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.homework.R
import com.example.homework.data.remote.response.InfoById
import com.example.homework.data.remote.response.SimilarItem
import com.example.homework.databinding.ItemMovieBinding
import javax.inject.Inject

class SimilarMoviesAdapter @Inject constructor(
    private val onMovieClick: (Int) -> Unit
) :
    RecyclerView.Adapter<SimilarMoviesAdapter.SimilarMoviesViewHolder>() {

    private var similarMovies: List<SimilarItem> = emptyList()
    private var detailedMovies = listOf<InfoById>()

    class SimilarMoviesViewHolder(val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarMoviesViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SimilarMoviesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SimilarMoviesViewHolder, position: Int) {
        val movie = similarMovies[position]
        val detailedMovie = detailedMovies.find { it.kinopoiskId == movie.filmId }

        with(holder.binding) {
            titleTv.text = movie.nameRu
            Glide.with(holder.itemView.context)
                .load(movie.posterUrlPreview)
                .placeholder(R.color.gray_gag)
                .into(holder.binding.posterIv)

            if (detailedMovie != null) {
                val rating = detailedMovie.ratingKinopoisk?.toString()
                if (rating.isNullOrEmpty()) {
                    ratingBtn.visibility = View.GONE
                } else {
                    ratingBtn.visibility = View.VISIBLE
                    ratingBtn.text = rating
                }
                genreTv.text = detailedMovie.genres.joinToString(", ") { it.genre }
            }
            root.setOnClickListener {
                onMovieClick(movie.filmId)
            }
        }
    }

    override fun getItemCount(): Int {
        return similarMovies.size
    }

    fun updateSimilarMoviesList(newSimilarMovies: List<SimilarItem>) {
        val diffCallback = SimilarMoviesDiffCallback(similarMovies, newSimilarMovies)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        similarMovies = newSimilarMovies
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateDetailedMoviesList(detailedMovies: List<InfoById>) {
        this.detailedMovies = detailedMovies
        notifyDataSetChanged()
    }
}

class SimilarMoviesDiffCallback(
    private val oldList: List<SimilarItem>,
    private val newList: List<SimilarItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].filmId == newList[newItemPosition].filmId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}