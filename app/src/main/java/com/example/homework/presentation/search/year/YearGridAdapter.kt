package com.example.homework.presentation.search.year

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.homework.databinding.ItemYearSelectorBinding
import javax.inject.Inject

class YearGridAdapter @Inject constructor(
    private val years: List<Int>,
    private val onYearClick: (Int) -> Unit,
    fromYear: Int?,
    toYear: Int?
) : RecyclerView.Adapter<YearGridAdapter.YearViewHolder>() {

    private var selectedYear: Int? = null

    init {
        selectedYear = fromYear ?: toYear
    }

    inner class YearViewHolder(val binding: ItemYearSelectorBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YearViewHolder {
        val binding = ItemYearSelectorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return YearViewHolder(binding)
    }

    override fun onBindViewHolder(holder: YearViewHolder, position: Int) {
        val year = years[position]
        holder.binding.yearBtn.text = year.toString()

        holder.binding.yearBtn.isSelected = year == selectedYear

        holder.binding.yearBtn.setOnClickListener {
            if (selectedYear != year) {
                val previousSelectedPosition = years.indexOf(selectedYear)
                selectedYear = year
                if (previousSelectedPosition != -1) {
                    notifyItemChanged(previousSelectedPosition)
                }
                notifyItemChanged(position)
            }
            onYearClick(year)
        }
    }

    override fun getItemCount(): Int = years.size
}