package com.example.homework.presentation.basic.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.homework.R
import com.example.homework.data.remote.response.GalleryItem
import com.example.homework.databinding.ItemLargeGalleryBinding
import com.example.homework.databinding.ItemSmallGalleryBinding
import javax.inject.Inject

class GalleryAdapter @Inject constructor(
    private val onPhotoClick: (String) -> Unit
) :
    PagingDataAdapter<GalleryItem, RecyclerView.ViewHolder>(GalleryItemDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_SMALL = 1
        private const val VIEW_TYPE_LARGE = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if ((position + 1) % 3 == 0) VIEW_TYPE_LARGE else VIEW_TYPE_SMALL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_LARGE) {
            val binding = ItemLargeGalleryBinding.inflate(inflater, parent, false)
            LargePhotoViewHolder(binding)
        } else {
            val binding = ItemSmallGalleryBinding.inflate(inflater, parent, false)
            SmallPhotoViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val photo = getItem(position)
        if (photo != null) {
            when (holder) {
                is SmallPhotoViewHolder -> {
                    Glide.with(holder.itemView.context)
                        .load(photo.imageUrl)
                        .placeholder(R.color.gray_gag)
                        .into(holder.binding.galleryImageIv)
                    holder.itemView.setOnClickListener { onPhotoClick(photo.imageUrl) }
                }
                is LargePhotoViewHolder -> {
                    Glide.with(holder.itemView.context)
                        .load(photo.imageUrl)
                        .placeholder(R.color.gray_gag)
                        .into(holder.binding.galleryImageIv)
                    holder.itemView.setOnClickListener { onPhotoClick(photo.imageUrl) }
                }
            }
        }
    }

    inner class SmallPhotoViewHolder(val binding: ItemSmallGalleryBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class LargePhotoViewHolder(val binding: ItemLargeGalleryBinding) :
        RecyclerView.ViewHolder(binding.root)
}

class GalleryItemDiffCallback : DiffUtil.ItemCallback<GalleryItem>() {
    override fun areItemsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean {
        return oldItem.imageUrl == newItem.imageUrl
    }

    override fun areContentsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean {
        return oldItem == newItem
    }
}