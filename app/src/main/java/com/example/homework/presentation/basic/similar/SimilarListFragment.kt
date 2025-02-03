package com.example.homework.presentation.basic.similar

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.homework.R
import com.example.homework.databinding.FragmentListOfMoviesBinding
import com.example.homework.presentation.basic.movieDetails.SimilarMoviesAdapter
import com.example.homework.presentation.bottomSheet.error.ErrorBottomSheet
import com.example.homework.presentation.ui.decorations.MovieListItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SimilarListFragment : Fragment() {

    private val viewModel: SimilarListViewModel by viewModels()
    private lateinit var binding: FragmentListOfMoviesBinding
    private lateinit var similarAdapter: SimilarMoviesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListOfMoviesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleError()

        val toolbar: Toolbar = binding.toolbar
        binding.toolbarTitle.text = getString(R.string.similar_movies)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val movieId = arguments?.let { SimilarListFragmentArgs.fromBundle(it).kinopoiskId } ?: -1
        if (movieId != -1) {
            viewModel.loadSimilarMoviesData(movieId)
        }

        similarAdapter = SimilarMoviesAdapter { kinopoiskId ->
            navigateToMovieDetails(kinopoiskId)
        }

        binding.allMovesRv.adapter = similarAdapter
        binding.allMovesRv.layoutManager = GridLayoutManager(context, 2)

        val horizontalSpacing = resources.getDimensionPixelSize(R.dimen.large_spacing)
        val bottomSpacing = resources.getDimensionPixelSize(R.dimen.large_spacing)

        binding.allMovesRv.addItemDecoration(
            MovieListItemDecoration(horizontalSpacing, bottomSpacing)
        )

        viewModel.similarMovies.observe(viewLifecycleOwner) { similarMovies ->
            similarAdapter.updateSimilarMoviesList(similarMovies)
            viewModel.loadDetailedSimilarMovies(similarMovies)
        }

        viewModel.detailedSimilarMovies.observe(viewLifecycleOwner) { detailedMovies ->
            similarAdapter.updateDetailedMoviesList(detailedMovies)
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
        val action = SimilarListFragmentDirections.actionSimilarListFragmentToMovieDetailsFragment(
            kinopoiskId
        )
        findNavController().navigate(action)
    }

    private fun showErrorBottomSheet(errorMessage: String) {
        val errorBottomSheet = ErrorBottomSheet.newInstance(errorMessage)
        errorBottomSheet.show(childFragmentManager, "ErrorBottomSheet")
    }
}