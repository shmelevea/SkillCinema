package com.example.homework.presentation.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.homework.R
import com.example.homework.entity.CategoryItem
import com.example.homework.databinding.ItemDelMoviesBtnBinding
import com.example.homework.databinding.ItemMovieBinding
import com.example.homework.entity.CategoryEntity
import com.example.homework.entity.MovieEntity
import javax.inject.Inject

class ProfileMoviesAdapter @Inject constructor(
    private val categoryName: String,
    private val onMovieClick: (Int) -> Unit,
    private val onClearCollectionClick: (CategoryItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_MOVIE = 0
        private const val TYPE_BUTTON = 1
    }

    private var movies: List<MovieEntity> = emptyList()

    override fun getItemCount(): Int {
        return movies.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == movies.size) TYPE_BUTTON else TYPE_MOVIE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_MOVIE) {
            val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MovieViewHolder(binding)
        } else {
            val binding = ItemDelMoviesBtnBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ClearButtonViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_MOVIE) {
            val movie = movies[position]
            (holder as MovieViewHolder).bind(movie)
        } else {
            val categoryItem = CategoryItem(CategoryEntity(categoryName), count = 0)
            (holder as ClearButtonViewHolder).bind(categoryItem)
        }
    }

    fun updateMoviesList(newMovies: List<MovieEntity>) {
        movies = newMovies
        notifyDataSetChanged()
    }

    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieEntity) {
            with(binding) {
                titleTv.text = movie.nameRu
                Glide.with(itemView.context)
                    .load(movie.posterUrlPreview)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.gray_gag)
                    .into(posterIv)

                val rating = movie.ratingKinopoisk?.toString()
                if (rating.isNullOrEmpty()) {
                    ratingBtn.visibility = View.GONE
                } else {
                    ratingBtn.visibility = View.VISIBLE
                    ratingBtn.text = rating
                }
                genreTv.text = movie.genres

                root.setOnClickListener {
                    onMovieClick(movie.kinopoiskId)
                }
            }
        }
    }

    inner class ClearButtonViewHolder(private val binding: ItemDelMoviesBtnBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(categoryItem: CategoryItem) {
            binding.clearCollectionBtn.setOnClickListener {
                onClearCollectionClick(categoryItem)
            }
        }
    }
}