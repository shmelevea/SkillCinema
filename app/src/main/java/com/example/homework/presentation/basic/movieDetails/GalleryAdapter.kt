package com.example.homework.presentation.basic.movieDetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.homework.R
import com.example.homework.data.remote.response.GalleryItem
import com.example.homework.databinding.ItemGalleryBinding
import javax.inject.Inject

class GalleryAdapter @Inject constructor() : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    private var galleryItems: List<GalleryItem> = emptyList()

    class GalleryViewHolder(val binding: ItemGalleryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding = ItemGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GalleryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val galleryItem = galleryItems[position]
        Glide.with(holder.itemView.context)
            .load(galleryItem.imageUrl)
            .placeholder(R.color.gray_gag)
            .into(holder.binding.galleryImageIv)
    }

    override fun getItemCount(): Int {
        return galleryItems.size
    }

    fun updateGalleryList(newGalleryItems: List<GalleryItem>) {
        val diffCallback = GalleryDiffCallback(galleryItems, newGalleryItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        galleryItems = newGalleryItems
        diffResult.dispatchUpdatesTo(this)
    }
}

class GalleryDiffCallback(
    private val oldList: List<GalleryItem>,
    private val newList: List<GalleryItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].imageUrl == newList[newItemPosition].imageUrl
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
