package com.example.homework.presentation.basic.series

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.homework.R
import com.example.homework.data.remote.response.Episode
import com.example.homework.data.remote.response.SeasonsItem
import com.example.homework.databinding.ItemSeasonBinding
import com.example.homework.databinding.ItemSeasonHeaderBinding
import com.example.homework.utils.formatReleaseDate
import javax.inject.Inject

class SeriesAdapter @Inject constructor() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Any>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SEASON -> {
                val binding = ItemSeasonHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SeasonViewHolder(binding)
            }

            VIEW_TYPE_EPISODE -> {
                val binding =
                    ItemSeasonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                EpisodeViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is SeasonsItem -> VIEW_TYPE_SEASON
            is Episode -> VIEW_TYPE_EPISODE
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SeasonViewHolder -> holder.bind(items[position] as SeasonsItem)
            is EpisodeViewHolder -> holder.bind(items[position] as Episode)
        }
    }

    inner class SeasonViewHolder(private val binding: ItemSeasonHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(season: SeasonsItem) {
            val context = itemView.context
            val episodeCountText = context.resources.getQuantityString(
                R.plurals.episodes_count,
                season.episodes.size,
                season.episodes.size
            )

            binding.episodesSumTv.text = context.getString(
                R.string.season_episode_count,
                season.number,
                episodeCountText
            )
        }
    }

    override fun getItemCount(): Int = items.size

    fun setItems(seasons: List<SeasonsItem>) {
        items.clear()
        seasons.forEach { season ->
            items.add(season)
            items.addAll(season.episodes)
        }
        notifyDataSetChanged()
    }

    inner class EpisodeViewHolder(private val binding: ItemSeasonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(episode: Episode) {
            binding.seasonTitleTv.text = when {
                !episode.nameRu.isNullOrBlank() -> episode.nameRu
                !episode.nameEn.isNullOrBlank() -> episode.nameEn
                else -> "No name"
            }
            binding.releaseDateTv.text = if (episode.releaseDate != null) {
                formatReleaseDate(episode.releaseDate)
            } else "Unknown date"
        }
    }

    companion object {
        private const val VIEW_TYPE_SEASON = 0
        private const val VIEW_TYPE_EPISODE = 1
    }
}