package com.example.homework.presentation.bottomSheet.collection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.homework.databinding.ItemCatCollectionBinding
import com.example.homework.databinding.ItemCreateOwnCollectionBinding
import com.example.homework.entity.CategoryItem
import com.example.homework.utils.getDisplayName
import javax.inject.Inject

class CollectionAdapter @Inject constructor(
    private var collections: List<CategoryItem>,
    private var movieCategoryStatusMap: Map<String, Boolean>,
    private val onCollectionClicked: (CategoryItem, Boolean) -> Unit,
    private val onCreateNewCollectionClicked: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
        const val TYPE_COLLECTION = 0
        const val TYPE_CREATE_COLLECTION = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == collections.size) {
            TYPE_CREATE_COLLECTION
        } else {
            TYPE_COLLECTION
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_COLLECTION -> CollectionViewHolder(
                ItemCatCollectionBinding.inflate(inflater, parent, false)
            )
            TYPE_CREATE_COLLECTION -> CreateCollectionViewHolder(
                ItemCreateOwnCollectionBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CollectionViewHolder -> holder.bind(collections[position])
            is CreateCollectionViewHolder -> holder.bind()
        }
    }

    override fun getItemCount(): Int = collections.size + 1

    fun updateCollections(newCollections: List<CategoryItem>, newMovieCategoryStatusMap: Map<String, Boolean>) {
        collections = newCollections
        movieCategoryStatusMap = newMovieCategoryStatusMap
        notifyDataSetChanged()
    }

    inner class CollectionViewHolder(private val binding: ItemCatCollectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(collection: CategoryItem) {
            binding.collectionCatTv.text = getDisplayName(binding.root.context, collection.category.name)
            binding.colCatSumTv.text = collection.count.toString()

            val isSelected = movieCategoryStatusMap[collection.category.name] ?: false
            binding.markBtn.isSelected = isSelected

            binding.markBtn.setOnClickListener {
                val newState = !binding.markBtn.isSelected
                binding.markBtn.isSelected = newState
                onCollectionClicked(collection, newState)
            }
        }
    }

    inner class CreateCollectionViewHolder(private val binding: ItemCreateOwnCollectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.root.setOnClickListener {
                onCreateNewCollectionClicked()
            }
        }
    }
}