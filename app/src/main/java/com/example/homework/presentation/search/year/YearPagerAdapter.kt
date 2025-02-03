package com.example.homework.presentation.search.year

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homework.databinding.YearGridRvBinding
import javax.inject.Inject

class YearPagerAdapter @Inject constructor(
    private val years: List<List<Int>>,
    private val onYearClick: (Int) -> Unit,
    private val fromYear: Int?,
    private val toYear: Int?
) : RecyclerView.Adapter<YearPagerAdapter.YearPageViewHolder>() {

    inner class YearPageViewHolder(binding: YearGridRvBinding) : RecyclerView.ViewHolder(binding.root) {
        val recyclerView: RecyclerView = binding.yearGridRv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YearPageViewHolder {
        val binding = YearGridRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return YearPageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: YearPageViewHolder, position: Int) {
        val adapter = YearGridAdapter(years[position], onYearClick, fromYear, toYear)
        holder.recyclerView.layoutManager = GridLayoutManager(holder.itemView.context, 3)
        holder.recyclerView.adapter = adapter
    }

    override fun getItemCount(): Int = years.size
}