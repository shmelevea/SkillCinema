package com.example.homework.presentation.search.year

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.homework.R
import com.example.homework.databinding.FragmentYearSelectorBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class YearSelectorFragment : Fragment() {

    private lateinit var binding: FragmentYearSelectorBinding
    private val viewModel: YearSelectorViewModel by viewModels()

    private val currentYear = LocalDate.now().year
    private val years = (1950..currentYear).toList()
    private val yearsPerPage = 12
    private val yearPages = years.chunked(yearsPerPage)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentYearSelectorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.apply {
            binding.toolbarTitle.text = getString(R.string.period)
            setNavigationOnClickListener { findNavController().navigateUp() }
        }

        viewModel.fromYearPage.observe(viewLifecycleOwner) { page ->
            if (binding.fromYearCard.yearVp.currentItem != page) {
                binding.fromYearCard.yearVp.setCurrentItem(page, true)
            }
            updatePeriodText()
        }

        viewModel.toYearPage.observe(viewLifecycleOwner) { page ->
            if (binding.toYearCard.yearVp.currentItem != page) {
                binding.toYearCard.yearVp.setCurrentItem(page, true)
            }
            updatePeriodText()
        }

        val fromYearAdapter = YearPagerAdapter(
            yearPages,
            onYearClick = { year -> handleFromYearSelection(year) },
            fromYear = viewModel.fromYear.value,
            toYear = null
        )

        val toYearAdapter = YearPagerAdapter(
            yearPages,
            onYearClick = { year -> handleToYearSelection(year) },
            fromYear = null,
            toYear = viewModel.toYear.value
        )

        binding.fromYearCard.apply {
            yearVp.adapter = fromYearAdapter

            leftBtn.setOnClickListener {
                val currentPage = viewModel.fromYearPage.value ?: 0
                if (currentPage > 0) {
                    yearVp.setCurrentItem(currentPage - 1, true)
                    viewModel.setFromYearPage(currentPage - 1)
                }
            }

            rightBtn.setOnClickListener {
                val currentPage = viewModel.fromYearPage.value ?: 0
                if (currentPage < yearPages.size - 1) {
                    yearVp.setCurrentItem(currentPage + 1, true)
                    viewModel.setFromYearPage(currentPage + 1)
                }
            }

            yearVp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.setFromYearPage(position)
                    updatePeriodText()
                }
            })
        }

        binding.toYearCard.apply {
            yearVp.adapter = toYearAdapter

            leftBtn.setOnClickListener {
                val currentPage = viewModel.toYearPage.value ?: 0
                if (currentPage > 0) {
                    yearVp.setCurrentItem(currentPage - 1, true)
                    viewModel.setToYearPage(currentPage - 1)
                }
            }

            rightBtn.setOnClickListener {
                val currentPage = viewModel.toYearPage.value ?: 0
                if (currentPage < yearPages.size - 1) {
                    yearVp.setCurrentItem(currentPage + 1, true)
                    viewModel.setToYearPage(currentPage + 1)
                }
            }

            yearVp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.setToYearPage(position)
                    updatePeriodText()
                }
            })
        }

        updatePeriodText()

        binding.selectBtn.setOnClickListener {
            val result = Bundle().apply {
                putInt("yearFrom", viewModel.fromYear.value ?: 1950)
                putInt("yearTo", viewModel.toYear.value ?: LocalDate.now().year)
            }
            parentFragmentManager.setFragmentResult("yearSelectionKey", result)
            findNavController().popBackStack()
        }
    }

    private fun updatePeriodText() {
        val fromPage = yearPages[viewModel.fromYearPage.value ?: 0]
        val fromYearText = "${fromPage.first()} - ${fromPage.last()}"
        binding.fromYearCard.selectedPeriodTv.text = fromYearText

        val toPage = yearPages[viewModel.toYearPage.value ?: 0]
        val toYearText = "${toPage.first()} - ${toPage.last()}"
        binding.toYearCard.selectedPeriodTv.text = toYearText
    }

    private fun handleYearSelection(selectedYear: Int, isFromYear: Boolean) {
        if (isFromYear) {
            viewModel.setFromYear(selectedYear)
            viewModel.toYear.value?.let { toYear ->
                if (toYear < selectedYear) {
                    resetYearsAndNotifyAdapters()
                }
            }
        } else {
            viewModel.setToYear(selectedYear)
            viewModel.fromYear.value?.let { fromYear ->
                if (fromYear > selectedYear) {
                    resetYearsAndNotifyAdapters()
                }
            }
        }
    }

    private fun resetYearsAndNotifyAdapters() {
        viewModel.setFromYear(null)
        viewModel.setToYear(null)

        binding.toYearCard.yearVp.adapter = YearPagerAdapter(
            yearPages,
            onYearClick = { year -> handleToYearSelection(year) },
            fromYear = null,
            toYear = viewModel.fromYear.value
        )
        binding.fromYearCard.yearVp.adapter = YearPagerAdapter(
            yearPages,
            onYearClick = { year -> handleFromYearSelection(year) },
            fromYear = viewModel.toYear.value,
            toYear = null
        )
    }

    private fun handleFromYearSelection(year: Int) {
        handleYearSelection(year, isFromYear = true)
    }

    private fun handleToYearSelection(year: Int) {
        handleYearSelection(year, isFromYear = false)
    }
}