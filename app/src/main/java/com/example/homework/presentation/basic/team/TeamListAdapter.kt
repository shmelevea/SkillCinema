package com.example.homework.presentation.basic.team

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.homework.R
import com.example.homework.data.remote.response.TeamItem
import com.example.homework.databinding.ItemPersonBinding
import javax.inject.Inject

class TeamListAdapter @Inject constructor(
    private val onPersonClick: (Int) -> Unit
) : RecyclerView.Adapter<TeamListAdapter.TeamViewHolder>() {

    private var teamList: List<TeamItem> = emptyList()

    class TeamViewHolder(
        private val binding: ItemPersonBinding,
        private val onPersonClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TeamItem) {
            val layoutParams = binding.root.layoutParams
            layoutParams.width = (308 * binding.root.context.resources.displayMetrics.density).toInt()
            binding.root.layoutParams = layoutParams

            binding.nameTv.text = when {
                item.nameRu.isNotBlank() -> item.nameRu
                item.nameEn.isNotBlank() -> item.nameEn
                else -> "Unknown"
            }

            binding.characterTv.text = item.description.takeIf { it?.isNotBlank() == true }
                ?: item.professionText

            Glide.with(binding.root.context)
                .load(item.posterUrl)
                .placeholder(R.color.gray_gag)
                .into(binding.personIv)

            binding.root.setOnClickListener {
                onPersonClick(item.staffId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val binding = ItemPersonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeamViewHolder(binding, onPersonClick)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bind(teamList[position])
    }

    override fun getItemCount(): Int = teamList.size

    fun updateList(newList: List<TeamItem>) {
        val diffCallback = TeamListDiffCallback(teamList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        teamList = newList
        diffResult.dispatchUpdatesTo(this)
    }
}

class TeamListDiffCallback(
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