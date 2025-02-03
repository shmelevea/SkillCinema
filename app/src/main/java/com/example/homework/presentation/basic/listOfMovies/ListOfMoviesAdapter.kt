package com.example.homework.presentation.basic.listOfMovies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.homework.R
import com.example.homework.data.remote.dto.MovieItem
import com.example.homework.databinding.ItemMovieBinding
import javax.inject.Inject

class ListOfMoviesAdapter @Inject constructor(
    private var movies: List<MovieItem>,
    private val onMovieClick: (Int) -> Unit
) : RecyclerView.Adapter<ListOfMoviesAdapter.HorizontalViewHolder>() {

    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HorizontalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HorizontalViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
    }

    fun updateMovies(newMovies: List<MovieItem>) {
        val diffCallback = MovieListDiffCallback(movies, newMovies)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        movies = newMovies
        diffResult.dispatchUpdatesTo(this)
    }

    inner class HorizontalViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieItem) {
            val movieTitle = if (!movie.nameRu.isNullOrBlank()) movie.nameRu else movie.nameOriginal
            binding.titleTv.text = movieTitle
            binding.genreTv.text = movie.genres.joinToString { it.genre }
            Glide.with(binding.root)
                .load(movie.posterUrlPreview)
                .placeholder(R.color.gray_gag)
                .into(binding.posterIv)
            val rating = movie.ratingKinopoisk?.toString()
            if (rating.isNullOrEmpty()) {
                binding.ratingBtn.visibility = View.GONE
            } else {
                binding.ratingBtn.visibility = View.VISIBLE
                binding.ratingBtn.text = rating
            }
            binding.root.setOnClickListener {
                onMovieClick(movie.kinopoiskId)
            }
        }
    }
}

class MovieListDiffCallback(
    private val oldList: List<MovieItem>,
    private val newList: List<MovieItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].kinopoiskId == newList[newItemPosition].kinopoiskId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}