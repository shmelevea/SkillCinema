package com.example.homework.presentation.basic.movieDetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.homework.R
import com.example.homework.data.remote.response.TeamItem
import com.example.homework.databinding.ItemPersonBinding
import javax.inject.Inject

class TeamAdapter @Inject constructor(
    private val isActor: Boolean,
    private val onPersonClick: (Int) -> Unit
) : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    private var teamList: List<TeamItem> = emptyList()

    class TeamViewHolder(val binding: ItemPersonBinding) : RecyclerView.ViewHolder(binding.root)

    private val filteredList: List<TeamItem>
        get() = teamList.filter { if (isActor) it.professionKey == "ACTOR" else it.professionKey != "ACTOR" }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val binding = ItemPersonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        val item = filteredList[position]

        with(holder.binding) {
            nameTv.text = when {
                item.nameRu.isNotBlank() -> item.nameRu
                item.nameEn.isNotBlank() -> item.nameEn
                else -> "Unknown"
            }

            characterTv.text = when {
                item.description.isNullOrEmpty() -> item.professionText
                else -> item.description
            }

            Glide.with(holder.itemView.context)
                .load(item.posterUrl)
                .placeholder(R.color.gray_gag)
                .into(personIv)

            root.setOnClickListener {
                onPersonClick(item.staffId)
            }
        }
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    fun updateList(newList: List<TeamItem>) {
        val uniqueList = newList.distinctBy { it.staffId }

        val diffCallback = TeamDiffCallback(teamList, uniqueList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        teamList = uniqueList
        diffResult.dispatchUpdatesTo(this)
    }
}

class TeamDiffCallback(
    private val oldList: List<TeamItem>,
    private val newList: List<TeamItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].staffId == newList[newItemPosition].staffId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}