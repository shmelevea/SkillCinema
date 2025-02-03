package com.example.homework.presentation.basic.homepage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.homework.R
import com.example.homework.data.remote.dto.MovieItem
import com.example.homework.databinding.ItemAllMoviesBtnBinding
import com.example.homework.databinding.ItemMovieBinding
import javax.inject.Inject

class MovieAdapter @Inject constructor(
    private var movies: List<MovieItem>,
    private val viewModel: HomeViewModel,
    private val onMovieClick: (Int) -> Unit,
    private val onShowAllClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_MOVIE = 0
        private const val TYPE_BUTTON = 1
    }

    override fun getItemCount(): Int {
        return movies.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == movies.size) {
            TYPE_BUTTON
        } else {
            TYPE_MOVIE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_MOVIE) {
            val binding =
                ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            HorizontalViewHolder(binding)
        } else {
            val binding =
                ItemAllMoviesBtnBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            HorizontalViewHolderButton(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_MOVIE) {
            val movie = movies[position]
            (holder as HorizontalViewHolder).bind(movie)
        } else {
            (holder as HorizontalViewHolderButton).bind()
        }
    }

    fun updateMovies(newMovies: List<MovieItem>) {
        val diffCallback = MovieDiffCallback(movies, newMovies)
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

            viewModel.isMovieViewed(movie.kinopoiskId).observeForever { isViewed ->
                if (isViewed) {
                    binding.apply {
                        eyeBtn.visibility = View.VISIBLE
                        gradientVw.visibility= View.VISIBLE
                    }
                }
            }
        }
    }

    inner class HorizontalViewHolderButton(private val binding: ItemAllMoviesBtnBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.allMovesBtn.setOnClickListener {
                onShowAllClick()
            }
        }
    }
}

class MovieDiffCallback(
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