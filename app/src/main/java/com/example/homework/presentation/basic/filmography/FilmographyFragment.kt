package com.example.homework.presentation.basic.filmography

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homework.R
import com.example.homework.databinding.FragmentUniversalPageBinding
import com.example.homework.presentation.bottomSheet.error.ErrorBottomSheet
import com.example.homework.presentation.ui.decorations.BottomOffsetItemDecoration
import com.example.homework.utils.getNameCategory
import com.example.homework.utils.getStyledText
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilmographyFragment : Fragment() {

    private val viewModel: FilmographyViewModel by viewModels()
    private lateinit var binding: FragmentUniversalPageBinding
    private lateinit var filmographyAdapter: FilmographyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUniversalPageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            if (!viewModel.isErrorShown) {
                val errorMessage = getString(R.string.error_msg)
                showErrorBottomSheet(errorMessage)
                viewModel.isErrorShown = true
            }
        }

        binding.toolbar.apply {
            binding.toolbarTitle.text = getString(R.string.filmography)
            setNavigationOnClickListener { findNavController().navigateUp() }
        }

        val personId = FilmographyFragmentArgs.fromBundle(requireArguments()).personId
        viewModel.loadFilmography(personId)

        setupChipGroup()

        filmographyAdapter = FilmographyAdapter { filmId ->
            navigateToMovieDetails(filmId)
        }

        val bottomOffset = resources.getDimensionPixelSize(R.dimen.small_spacing)
        val rightMargin = resources.getDimensionPixelSize(R.dimen.spacing_filmography_rv)

        binding.contentRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = filmographyAdapter
            addItemDecoration(BottomOffsetItemDecoration(bottomOffset))

            val params = layoutParams as ConstraintLayout.LayoutParams
            params.marginEnd = rightMargin
            layoutParams = params
        }

        lifecycleScope.launch {
            viewModel.pagedFilms.asFlow().collectLatest { pagedData ->
                filmographyAdapter.submitData(pagedData)
            }
        }

        viewModel.filmography.observe(viewLifecycleOwner) { personInfo ->
            binding.nameTv.apply {
                text = personInfo.nameRu?.takeIf { it.isNotBlank() }
                    ?: personInfo.nameEn?.takeIf { it.isNotBlank() } ?: ""
                visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
            }
        }

        viewModel.detailedMovies.observe(viewLifecycleOwner) { detailedMovies ->
            filmographyAdapter.updateDetailedMoviesList(detailedMovies)
        }
    }

    private fun setupChipGroup() {
        binding.categoryCg.apply {
            visibility = View.VISIBLE
            removeAllViews()

            viewModel.filmsByProfession.observe(viewLifecycleOwner) { filmsByProfession ->
                viewModel.selectedCategory.observe(viewLifecycleOwner) { selectedCategory ->
                    removeAllViews()

                    filmsByProfession.forEach { (category, filmsData) ->
                        val (_, count) = filmsData
                        val chip = Chip(requireContext()).apply {
                            text = getStyledText(requireContext(), getFilmographyCategory(category), count)
                            isCheckable = true
                            isChecked = (category == selectedCategory)
                            setOnClickListener {
                                if (category != selectedCategory) {
                                    viewModel.setSelectedCategory(category)
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

    private fun getFilmographyCategory(category: String): String {
        return getNameCategory(requireContext(), category)
    }

    private fun navigateToMovieDetails(filmId: Int) {
        val action =
            FilmographyFragmentDirections.actionFilmographyFragmentToMovieDetailsFragment(filmId)
        findNavController().navigate(action)

    }

    private fun showErrorBottomSheet(errorMessage: String) {
        val errorBottomSheet = ErrorBottomSheet.newInstance(errorMessage)
        errorBottomSheet.show(childFragmentManager, "ErrorBottomSheet")
    }
}