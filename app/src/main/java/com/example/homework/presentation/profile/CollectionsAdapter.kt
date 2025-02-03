package com.example.homework.presentation.profile

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.homework.R
import com.example.homework.entity.CategoryItem
import com.example.homework.databinding.ItemCollectionSquareBinding
import com.example.homework.utils.getDisplayName
import javax.inject.Inject

class CollectionsAdapter @Inject constructor(
    private val onMovieClick: (String) -> Unit,
    private val onCategoryRemoved: (CategoryItem) -> Unit,
    private val itemWidth: Int
) : RecyclerView.Adapter<CollectionsAdapter.CollectionsAdapterViewHolder>() {

    private var categories: List<CategoryItem> = emptyList()

    fun setCategories(newCategories: List<CategoryItem>) {
        categories = newCategories
        notifyDataSetChanged()
    }

    class CollectionsAdapterViewHolder(val binding: ItemCollectionSquareBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CollectionsAdapterViewHolder {
        val binding =
            ItemCollectionSquareBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollectionsAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CollectionsAdapterViewHolder,
        position: Int
    ) {
        val categoryItem = categories[position]

        val layoutParams = holder.itemView.layoutParams
        layoutParams.width = itemWidth
        layoutParams.height = itemWidth
        holder.itemView.layoutParams = layoutParams

        val displayName = getDisplayName(holder.itemView.context, categoryItem.category.name)

        holder.binding.squareTv.text = displayName
        holder.binding.countTv.text = categoryItem.count.toString()

        when (categoryItem.category.name) {
            "liked" -> {
                holder.binding.iconBtn.setImageResource(R.drawable.ic_heart)
                holder.binding.closeBtn.visibility = GONE
            }
            "wishlist" -> {
                holder.binding.iconBtn.setImageResource(R.drawable.ic_flag)
                holder.binding.closeBtn.visibility = GONE
            }
            else -> holder.binding.iconBtn.setImageResource(R.drawable.ic_profile)
        }

        holder.itemView.setOnClickListener {
            onMovieClick(categoryItem.category.name)
        }
        holder.binding.closeBtn.setOnClickListener {
            onCategoryRemoved(categoryItem)
        }
    }

    override fun getItemCount(): Int = categories.size
}