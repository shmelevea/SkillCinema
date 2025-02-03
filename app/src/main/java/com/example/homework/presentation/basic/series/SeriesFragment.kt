package com.example.homework.presentation.basic.series

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homework.R
import com.example.homework.databinding.FragmentUniversalPageBinding
import com.example.homework.presentation.bottomSheet.error.ErrorBottomSheet
import com.example.homework.presentation.ui.decorations.BottomOffsetItemDecoration
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeriesFragment : Fragment() {

    private val viewModel: SeriesViewModel by viewModels()
    private lateinit var binding: FragmentUniversalPageBinding
    private lateinit var seriesAdapter: SeriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUniversalPageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        handleError()

        binding.toolbar.apply {
            setNavigationOnClickListener { findNavController().navigateUp() }
        }

        binding.seasonsTv.visibility = View.VISIBLE
        val movieId = SeriesFragmentArgs.fromBundle(requireArguments()).kinopoiskId

        observeViewModel()
        viewModel.getSeasons(movieId)

        setupSeriesAdapter()
        setupChipGroup()
    }

    private fun handleError() {
        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            if (!viewModel.isErrorShown) {
                val errorMessage = getString(R.string.error_msg)
                showErrorBottomSheet(errorMessage)
                viewModel.isErrorShown = true
            }
        }
    }

    private fun setupSeriesAdapter() {
        seriesAdapter = SeriesAdapter()
        binding.contentRv.apply {
            val bottomOffset = resources.getDimensionPixelSize(R.dimen.small_spacing)
            layoutManager = LinearLayoutManager(context)
            adapter = seriesAdapter
            addItemDecoration(BottomOffsetItemDecoration(bottomOffset))
        }
    }

    private fun setupChipGroup() {
        viewModel.seasons.observe(viewLifecycleOwner) { seasons ->
            viewModel.selectedSeason.observe(viewLifecycleOwner) { selectedSeason ->
                binding.categoryCg.apply {
                    visibility = View.VISIBLE
                    removeAllViews()

                    seasons.forEach { season ->
                        val chip = Chip(requireContext()).apply {
                            text = season.number.toString()
                            isCheckable = true
                            isChecked = season.number == selectedSeason
                            setOnClickListener {
                                if (season.number != selectedSeason) {
                                    viewModel.setSelectedSeason(season.number)
                                } else {
                                    isChecked = true
                                }
                            }
                        }
                        addView(chip)
                    }
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.filmDetails.observe(viewLifecycleOwner) { filmDetails ->
            binding.toolbarTitle.text =
                filmDetails?.nameRu ?: filmDetails?.nameEn ?: filmDetails?.nameOriginal
        }

        viewModel.selectedSeason.observe(viewLifecycleOwner) { seasonNumber ->
            viewModel.seasons.value?.firstOrNull { it.number == seasonNumber }
                ?.let { selectedSeason ->
                    seriesAdapter.setItems(listOf(selectedSeason))
                }
        }
    }

    private fun showErrorBottomSheet(errorMessage: String) {
        val errorBottomSheet = ErrorBottomSheet.newInstance(errorMessage)
        errorBottomSheet.show(childFragmentManager, "ErrorBottomSheet")
    }
}