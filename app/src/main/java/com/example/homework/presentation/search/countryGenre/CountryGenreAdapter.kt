package com.example.homework.presentation.search.countryGenre

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.homework.data.remote.CountryGenreItem
import com.example.homework.databinding.ItemCountryGenreBinding

class CountryGenreAdapter(
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<CountryGenreAdapter.CountryGenreViewHolder>() {

    private val items = mutableListOf<CountryGenreItem>()

    fun getCurrentList(): List<CountryGenreItem> = items

    fun submitList(newItems: List<CountryGenreItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class CountryGenreViewHolder(
        private val binding: ItemCountryGenreBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CountryGenreItem) {
            binding.searchCatTv.text = item.name
            binding.root.setOnClickListener { onClick(item.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryGenreViewHolder {
        val binding = ItemCountryGenreBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CountryGenreViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CountryGenreViewHolder, position: Int) {
        holder.bind(items[position])
    }
}