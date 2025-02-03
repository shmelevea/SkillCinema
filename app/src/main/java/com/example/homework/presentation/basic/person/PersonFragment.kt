package com.example.homework.presentation.basic.person

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.homework.R
import com.example.homework.databinding.FragmentPersonBinding
import com.example.homework.presentation.bottomSheet.error.ErrorBottomSheet
import com.example.homework.presentation.ui.decorations.EndOffsetItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersonFragment : Fragment() {

    private val viewModel: PersonViewModel by viewModels()
    private lateinit var binding: FragmentPersonBinding
    private lateinit var personAdapter: PersonAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPersonBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleError()

        val toolbar: Toolbar = binding.toolbar
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val personId = PersonFragmentArgs.fromBundle(requireArguments()).personId
        viewModel.loadPersonInfo(personId)

        personAdapter = PersonAdapter { kinopoiskId ->
            navigateToMovieDetails(kinopoiskId)
        }

        binding.bestInclude.apply {
            sectionTitleTv.text = getString(R.string.best)
            sumAllTv.visibility = View.GONE
            showAllBtn.visibility = View.GONE
        }
        binding.bestInclude.sectionRv.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val endOffset = resources.getDimensionPixelSize(R.dimen.small_spacing)
            addItemDecoration(EndOffsetItemDecoration(endOffset))
            adapter = personAdapter
        }

        viewModel.personInfo.observe(viewLifecycleOwner) { personInfo ->
            val topRatedFilms = personInfo.films
                .filter { it.rating?.toDoubleOrNull() != null }
                .sortedByDescending { it.rating?.toDouble() }
                .take(10)

            personAdapter.updateFilms(topRatedFilms)
            viewModel.loadDetailedMovies(topRatedFilms.map { it.filmId })

            binding.sumTv.text = resources.getQuantityString(
                R.plurals.film_count,
                personInfo.films.size,
                personInfo.films.size
            )

            binding.nameTv.text = when {
                !personInfo.nameRu.isNullOrBlank() -> personInfo.nameRu
                !personInfo.nameEn.isNullOrBlank() -> personInfo.nameEn
                else -> "Unknown Name"
            }
            Glide.with(requireContext())
                .load(personInfo.posterUrl)
                .into(binding.galleryImageIv)
            binding.characterTv.text = personInfo.profession

            binding.galleryImageIv.setOnClickListener {
                navigateToFullScreenImage(personInfo.posterUrl)
            }
        }

        viewModel.detailedMovies.observe(viewLifecycleOwner) { detailedMovies ->
            personAdapter.updateDetailedMoviesList(detailedMovies)
        }

        binding.apply {
            allBtn.setOnClickListener {
                navigateToFilmography(personId)
            }
            allTv.setOnClickListener {
                navigateToFilmography(personId)
            }
        }
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

    private fun navigateToMovieDetails(kinopoiskId: Int) {
        val action =
            PersonFragmentDirections.actionPersonFragmentToMovieDetailsFragment(kinopoiskId)
        findNavController().navigate(action)

    }

    private fun navigateToFilmography(personId: Int) {
        val action = PersonFragmentDirections.actionPersonFragmentToFilmographyFragment(personId)
        findNavController().navigate(action)
    }

    private fun navigateToFullScreenImage(imageUrl: String) {
        val action =
            PersonFragmentDirections.actionPersonFragmentToFullScreenImageFragment(imageUrl)
        findNavController().navigate(action)
    }

    private fun showErrorBottomSheet(errorMessage: String) {
        val errorBottomSheet = ErrorBottomSheet.newInstance(errorMessage)
        errorBottomSheet.show(childFragmentManager, "ErrorBottomSheet")
    }
}