package com.example.homework.presentation.search.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.homework.R
import com.example.homework.databinding.FragmentSettingSearchBinding
import com.example.homework.presentation.search.SearchFilterSharedViewModel
import com.example.homework.utils.ALL
import com.example.homework.utils.FILM
import com.example.homework.utils.NUM_VOTE
import com.example.homework.utils.RATING
import com.example.homework.utils.TV_SERIES
import com.example.homework.utils.YEAR
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class SettingSearchFragment : Fragment() {

    private lateinit var binding: FragmentSettingSearchBinding
    private val sharedViewModel: SearchFilterSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.apply {
            binding.toolbarTitle.text = getString(R.string.search_settings)
            setNavigationOnClickListener { findNavController().navigateUp() }
        }

        initializeButtonLabels()
        setupCategoryFilters()
        setupResultListeners()

    }

    private fun setupResultListeners() {
        parentFragmentManager.setFragmentResultListener(
            "countryGenreRequestKey",
            viewLifecycleOwner
        ) { _, bundle ->
            val selectedItemId = bundle.getInt("selectedItemId", -1)
            val selectedItem = bundle.getString("selectedItem")
            val isCountry = bundle.getBoolean("isCountry")

            if (isCountry) {
                binding.countrySet.setTv.text = selectedItem
                sharedViewModel.setSelectedCountry(selectedItem)
                sharedViewModel.setSelectedCountryId(selectedItemId)
            } else {
                binding.genreSet.setTv.text = selectedItem
                sharedViewModel.setSelectedGenre(selectedItem)
                sharedViewModel.setSelectedGenreId(selectedItemId)
            }
        }

        parentFragmentManager.setFragmentResultListener(
            "yearSelectionKey",
            viewLifecycleOwner
        ) { _, bundle ->
            val yearFrom = bundle.getInt("yearFrom", 1950)
            val yearTo = bundle.getInt("yearTo", LocalDate.now().year)
            binding.yearSet.setTv.text =
                getString(R.string.selected_year_range, yearFrom, yearTo)
            sharedViewModel.setYearRange(yearFrom, yearTo)
        }
    }


    private fun setupCategoryFilters() {
        binding.categoryTg.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val type = when (checkedId) {
                    R.id.all_btn -> ALL
                    R.id.movies_btn -> FILM
                    R.id.tvseries_btn -> TV_SERIES
                    else -> ALL
                }
                sharedViewModel.setMovieType(type)

            }
        }

        binding.typeTg.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val order = when (checkedId) {
                    R.id.date_btn -> YEAR
                    R.id.popularity_btn -> NUM_VOTE
                    R.id.rating_btn -> RATING
                    else -> YEAR
                }
                sharedViewModel.setSortOrder(order)
            }
        }

        binding.rangeBar.apply {
            valueFrom = 0f
            valueTo = 10f
            stepSize = 1f
            addOnChangeListener { _, _, _ ->
                val ratingFrom = values[0]
                val ratingTo = values[1]
                sharedViewModel.setRatingRange(ratingFrom, ratingTo)

            }
        }

        binding.yearSet.setTv.setOnClickListener {
            val action =
                SettingSearchFragmentDirections.actionSettingSearchFragmentToYearSelectorFragment()
            findNavController().navigate(action)
        }

        binding.countrySet.setTv.setOnClickListener {
            val action =
                SettingSearchFragmentDirections.actionSettingSearchFragmentToCountryGenreFragment(
                    isCountryRequest = true
                )
            findNavController().navigate(action)
        }

        binding.genreSet.setTv.setOnClickListener {
            val action =
                SettingSearchFragmentDirections.actionSettingSearchFragmentToCountryGenreFragment(
                    isCountryRequest = false
                )
            findNavController().navigate(action)
        }
    }

    private fun initializeButtonLabels() {
        binding.apply {
            countrySet.catTv.text = getString(R.string.country)
            genreSet.catTv.text = getString(R.string.genre)
            yearSet.catTv.text = getString(R.string.year)
        }
    }

    private fun restoreState() {
        sharedViewModel.typeMovie.value.let { type ->
            val checkedId = when (type) {
                ALL -> R.id.all_btn
                FILM -> R.id.movies_btn
                TV_SERIES -> R.id.tvseries_btn
                else -> R.id.all_tv
            }
            binding.categoryTg.check(checkedId)
        }

        sharedViewModel.sortOrder.value.let { order ->
            val checkedId = when (order) {
                YEAR -> R.id.date_btn
                NUM_VOTE -> R.id.popularity_btn
                RATING -> R.id.rating_btn
                else -> R.id.date_btn
            }
            binding.typeTg.check(checkedId)
        }

        val ratingRange = sharedViewModel.ratingRange.value
        ratingRange.let { (ratingFrom, ratingTo) ->
            binding.rangeBar.setValues(ratingFrom, ratingTo)
        }

        sharedViewModel.selectedCountry.value?.let { country ->
            binding.countrySet.setTv.text = country
        }

        sharedViewModel.selectedGenre.value?.let { genre ->
            binding.genreSet.setTv.text = genre
        }

        sharedViewModel.yearRange.value.let { (yearFrom, yearTo) ->
            binding.yearSet.setTv.text =
                getString(R.string.selected_year_range, yearFrom, yearTo)
        }
    }

    override fun onResume() {
        super.onResume()
        restoreState()
    }
}