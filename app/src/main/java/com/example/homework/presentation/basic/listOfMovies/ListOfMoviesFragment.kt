package com.example.homework.presentation.basic.listOfMovies

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homework.R
import com.example.homework.databinding.FragmentListOfMoviesBinding
import com.example.homework.presentation.bottomSheet.error.ErrorBottomSheet
import com.example.homework.presentation.ui.decorations.MovieListItemDecoration
import com.example.homework.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListOfMoviesFragment : Fragment() {

    private val viewModel: ListOfMoviesViewModel by viewModels()
    private lateinit var binding: FragmentListOfMoviesBinding
    private lateinit var listAdapter: ListOfMoviesAdapter

    private val args: ListOfMoviesFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListOfMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleError()

        listAdapter = ListOfMoviesAdapter(emptyList()) { kinopoiskId ->
            navigateToMovieDetails(kinopoiskId)
        }

        binding.allMovesRv.apply {
            val horizontalSpacing = resources.getDimensionPixelSize(R.dimen.large_spacing)
            val bottomSpacing = resources.getDimensionPixelSize(R.dimen.large_spacing)
            adapter = listAdapter
            layoutManager = GridLayoutManager(context, 2)
            addItemDecoration(MovieListItemDecoration(horizontalSpacing, bottomSpacing))
        }

        val collectionName = args.collectionName
        val type = args.type
        val countryId = args.countryId
        val genreId = args.genreId

        if (collectionName.isNotEmpty()) {
            viewModel.fetchMoviesByCategory(collectionName)
            binding.toolbarTitle.text = getDisplayName(requireContext(), collectionName)
        } else {
            val categoryName = getCategoryName(type, genreId, countryId)
            binding.toolbarTitle.text = categoryName
            viewModel.fetchMovies(type, countryId, genreId)
        }

        viewModel.moviesLiveData.observe(viewLifecycleOwner) { movies ->
            listAdapter.updateMovies(movies)
        }

        val toolbar: Toolbar = binding.toolbar
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.allMovesRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!viewModel.isLoading && !viewModel.isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= viewModel.pageSize
                    ) {
                        viewModel.loadNextPage(type, countryId, genreId)
                    }
                }
            }
        })
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

    private fun getCategoryName(type: String, genreId: Int?, countryId: Int?): String {
        return when (type) {
            PREMIERE -> getString(R.string.premiere)
            TOP_250 -> getString(R.string.top_250)
            TV_SERIES -> getString(R.string.tv_series)
            else -> {
                val genre =
                    genreId?.let { genreList.find { it.id == genreId }?.plural } ?: "Unknown Genre"
                val country = countryId?.let { countryList.find { it.id == countryId }?.genitive }
                    ?: "Unknown Country"
                "$genre $country"
            }
        }
    }

    private fun navigateToMovieDetails(kinopoiskId: Int) {
        val action =
            ListOfMoviesFragmentDirections.actionListOfMoviesFragmentToMovieDetailsFragment(
                kinopoiskId
            )
        findNavController().navigate(action)
    }

    private fun showErrorBottomSheet(errorMessage: String) {
        val errorBottomSheet = ErrorBottomSheet.newInstance(errorMessage)
        errorBottomSheet.show(childFragmentManager, "ErrorBottomSheet")
    }
}