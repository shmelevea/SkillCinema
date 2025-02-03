package com.example.homework.presentation.basic.filmography

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.homework.R
import com.example.homework.data.remote.response.Film
import com.example.homework.data.remote.response.InfoById
import com.example.homework.databinding.ItemMovieHorizontalBinding
import javax.inject.Inject

class FilmographyAdapter @Inject constructor(
    private val onItemClick: (Int) -> Unit
) : PagingDataAdapter<Film, FilmographyAdapter.FilmographyViewHolder>(FilmographyDiffCallback()) {

    private var detailedMovies = listOf<InfoById>()

    class FilmographyViewHolder(val binding: ItemMovieHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmographyViewHolder {
        val binding =
            ItemMovieHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilmographyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilmographyViewHolder, position: Int) {
        val movie = getItem(position) ?: return
        val detailedMovie = detailedMovies.find { it.kinopoiskId == movie.filmId }

        with(holder.binding) {
            titleTv.text = (movie.nameRu ?: movie.nameEn).toString()

            val rating = movie.rating
            if (rating.isNullOrEmpty()) {
                ratingBtn.visibility = View.GONE
            } else {
                ratingBtn.visibility = View.VISIBLE
                ratingBtn.text = rating
            }

            Glide.with(holder.itemView.context)
                .load(detailedMovie?.posterUrlPreview)
                .placeholder(R.color.gray_gag)
                .into(posterIv)

            val year = detailedMovie?.year?.toString() ?: ""
            val genres = detailedMovie?.genres?.joinToString(", ") { it.genre } ?: ""
            genreTv.text = listOfNotNull(year, genres).joinToString(", ")

            holder.itemView.setOnClickListener {
                onItemClick(movie.filmId)
            }
        }
    }

    fun updateDetailedMoviesList(detailedMovies: List<InfoById>) {
        this.detailedMovies = detailedMovies
        notifyDataSetChanged()
    }
}

class FilmographyDiffCallback : DiffUtil.ItemCallback<Film>() {
    override fun areItemsTheSame(oldItem: Film, newItem: Film): Boolean {
        return oldItem.filmId == newItem.filmId
    }

    override fun areContentsTheSame(oldItem: Film, newItem: Film): Boolean {
        return oldItem == newItem
    }
}