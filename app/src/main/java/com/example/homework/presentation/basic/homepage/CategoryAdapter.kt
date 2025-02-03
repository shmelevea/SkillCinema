package com.example.homework.presentation.basic.homepage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homework.R
import com.example.homework.presentation.ui.decorations.EndOffsetItemDecoration
import com.example.homework.data.remote.dto.Category
import com.example.homework.databinding.ItemHomeVrvBinding
import com.example.homework.databinding.ItemLogoBinding
import javax.inject.Inject

class CategoryAdapter @Inject constructor(
    private var categories: List<Any>,
    private val onShowAllClicked: (Category) -> Unit,
    private val onMovieClicked: (Int) -> Unit,
    private val viewModel: HomeViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val viewTypeLogo = 0
    private val viewTypeCategory = 1

    override fun getItemCount(): Int = categories.size

    override fun getItemViewType(position: Int): Int {
        return if (categories[position] is Logo) viewTypeLogo else viewTypeCategory
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == viewTypeLogo) {
            val binding =
                ItemLogoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            LogoViewHolder(binding)
        } else {
            val binding =
                ItemHomeVrvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            VerticalViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VerticalViewHolder -> holder.bind(categories[position] as Category)
        }
    }

    fun updateCategories(newCategories: List<Category>) {
        val newList = listOf(Logo()) + newCategories
        val diffCallback = CategoryDiffCallback(categories, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        categories = newList
        diffResult.dispatchUpdatesTo(this)
    }

    inner class VerticalViewHolder(private val binding: ItemHomeVrvBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val viewPager: RecyclerView = binding.homeRvHorizontal
        private val moviesAdapter = MovieAdapter(
            emptyList(),
            viewModel = viewModel,
            onMovieClicked
        ) { onShowAllClicked(categories[bindingAdapterPosition] as Category) }

        init {
            viewPager.adapter = moviesAdapter
            val spacing = itemView.context.resources.getDimensionPixelSize(R.dimen.small_spacing)
            viewPager.layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            viewPager.addItemDecoration(EndOffsetItemDecoration(spacing))
        }

        fun bind(category: Category) {
            binding.categoryTv.text = category.categoryName
            moviesAdapter.updateMovies(category.movies)

            binding.allTv.setOnClickListener {
                onShowAllClicked(category)
            }
        }
    }

    inner class LogoViewHolder(binding: ItemLogoBinding) :
        RecyclerView.ViewHolder(binding.root)
}

class CategoryDiffCallback(
    private val oldList: List<Any>,
    private val newList: List<Any>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if (oldList[oldItemPosition] is Logo && newList[newItemPosition] is Logo) {
            return true
        }
        if (oldList[oldItemPosition] is Category && newList[newItemPosition] is Category) {
            return (oldList[oldItemPosition] as Category).categoryName == (newList[newItemPosition] as Category).categoryName
        }
        return false
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if (oldList[oldItemPosition] is Category && newList[newItemPosition] is Category) {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
        return true
    }
}

data class Logo(val name: String = "Logo")