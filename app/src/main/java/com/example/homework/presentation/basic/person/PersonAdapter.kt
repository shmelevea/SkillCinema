package com.example.homework.presentation.basic.person

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.homework.R
import com.example.homework.data.remote.response.Film
import com.example.homework.data.remote.response.InfoById
import com.example.homework.databinding.ItemMovieBinding
import javax.inject.Inject

class PersonAdapter @Inject constructor(
    private val onItemClick: (Int) -> Unit
) : ListAdapter<Film, PersonAdapter.PersonViewHolder>(FilmDiffCallback()) {

    private var detailedMovies = listOf<InfoById>()

    class PersonViewHolder(val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PersonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val movie = getItem(position)
        val detailedMovie = detailedMovies.find { it.kinopoiskId == movie.filmId }

        with(holder.binding) {
            titleTv.text = movie.nameRu ?: movie.nameEn
            val rating = movie.rating
            if (rating.isNullOrEmpty()) {
                ratingBtn.visibility = View.GONE
            } else {
                ratingBtn.visibility = View.VISIBLE
                ratingBtn.text = rating
            }
            if (detailedMovie != null) {
                Glide.with(holder.itemView.context)
                    .load(detailedMovie.posterUrlPreview)
                    .placeholder(R.color.gray_gag)
                    .into(holder.binding.posterIv)

                genreTv.text = detailedMovie.genres.joinToString(", ") { it.genre }
            }
            root.setOnClickListener {
                onItemClick(movie.filmId)
            }
        }
    }

    fun updateFilms(newFilms: List<Film>) {
        val uniqueFilms = newFilms.distinctBy { it.filmId }
        submitList(uniqueFilms)
    }

    fun updateDetailedMoviesList(detailedMovies: List<InfoById>) {
        this.detailedMovies = detailedMovies
        notifyDataSetChanged()
    }
}

class FilmDiffCallback : DiffUtil.ItemCallback<Film>() {
    override fun areItemsTheSame(oldItem: Film, newItem: Film): Boolean {
        return oldItem.filmId == newItem.filmId
    }

    override fun areContentsTheSame(oldItem: Film, newItem: Film): Boolean {
        return oldItem == newItem
    }
}