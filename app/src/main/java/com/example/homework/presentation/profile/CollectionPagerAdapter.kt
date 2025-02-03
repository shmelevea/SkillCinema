package com.example.homework.presentation.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homework.entity.CategoryItem
import com.example.homework.databinding.ItemCollectionPageBinding
import javax.inject.Inject

class CollectionPagerAdapter @Inject constructor(
    private val pages: List<List<CategoryItem>>,
    private val onMovieClick: (String) -> Unit,
    private val onCategoryRemoved: (CategoryItem) -> Unit
) : RecyclerView.Adapter<CollectionPagerAdapter.PageViewHolder>() {

    class PageViewHolder(val binding: ItemCollectionPageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val binding = ItemCollectionPageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        val pageCategories = pages[position]

        val screenWidth = holder.itemView.context.resources.displayMetrics.widthPixels

        val itemWidth = (screenWidth * 0.84 / 2).toInt()

        val adapter = CollectionsAdapter(onMovieClick, onCategoryRemoved, itemWidth)
        adapter.setCategories(pageCategories)
        holder.binding.collectionRv.adapter = adapter
        holder.binding.collectionRv.layoutManager = GridLayoutManager(holder.itemView.context, 2)
    }


    override fun getItemCount(): Int = pages.size
}