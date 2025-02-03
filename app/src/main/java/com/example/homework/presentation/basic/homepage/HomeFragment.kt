package com.example.homework.presentation.basic.homepage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.homework.R
import com.example.homework.data.remote.dto.*
import com.example.homework.databinding.FragmentHomeBinding
import com.example.homework.presentation.bottomSheet.error.ErrorBottomSheet
import com.example.homework.presentation.ui.decorations.BottomOffsetItemDecoration
import com.example.homework.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        handleError()

        categoryAdapter = CategoryAdapter(emptyList(), { category ->
            val type = when (category.categoryName) {
                getString(R.string.premiere) -> PREMIERE
                getString(R.string.top_250) -> TOP_250
                getString(R.string.tv_series) -> TV_SERIES
                else -> FILM
            }
            val countryId = category.countryId ?: -1
            val genreId = category.genreId ?: -1

            navigateToListOfMovies(type, countryId, genreId)
        }, { movieId ->
            navigateToMovieDetails(movieId)
        }, viewModel)

        val bottomOffset = resources.getDimensionPixelSize(R.dimen.home_vertical_spacing)
        binding.homeRv.apply {
            adapter = categoryAdapter
            addItemDecoration(BottomOffsetItemDecoration(bottomOffset))
        }

        binding.retryBtn.setOnClickListener {
            reloadData()
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

    private fun observeViewModel() {
        viewModel.combinedLiveData.observe(viewLifecycleOwner) {
            updateCategories(
                viewModel.premieresLiveData.value,
                viewModel.top250LiveData.value,
                viewModel.firstRandomMoviesLiveData.value,
                viewModel.secondRandomMoviesLiveData.value,
                viewModel.tvSeriesLiveData.value
            )
        }

        viewModel.isRetryButtonVisible.observe(viewLifecycleOwner) { isVisible ->
            binding.retryBtn.visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }

    private fun updateCategories(
        premieres: List<MovieItem>?,
        top250Movies: List<MovieItem>?,
        firstRandomContent: RandomContent?,
        secondRandomContent: RandomContent?,
        tvSeriesContent: RandomContent?
    ) {
        val categories = mutableListOf<Category>()

        addCategory(categories, premieres, getString(R.string.premiere))
        addCategory(categories, top250Movies, getString(R.string.top_250))

        firstRandomContent?.let {
            val firstRandomCategoryName = getCategoryName(it.genreId, it.countryId)
            addCategory(categories, it.movies, firstRandomCategoryName, it.countryId, it.genreId)
        }

        secondRandomContent?.let {
            val secondRandomCategoryName = getCategoryName(it.genreId, it.countryId)
            addCategory(categories, it.movies, secondRandomCategoryName, it.countryId, it.genreId)
        }

        tvSeriesContent?.let {
            addCategory(
                categories,
                it.movies,
                getString(R.string.tv_series),
                it.countryId,
                it.genreId
            )
        }

        categoryAdapter.updateCategories(categories)
    }

    private fun addCategory(
        categories: MutableList<Category>,
        movies: List<MovieItem>?,
        categoryName: String,
        countryId: Int? = null,
        genreId: Int? = null
    ) {
        movies?.let {
            val category = Category(
                categoryName = categoryName,
                movies = it,
                countryId = countryId,
                genreId = genreId
            )
            categories.add(category)
        }
    }

    private fun getCategoryName(genreId: Int?, countryId: Int?): String {
        val genre = genreId?.let { genreList.find { it.id == genreId }?.plural }
            ?: getString(R.string.unknown_genre)
        val country = countryId?.let { countryList.find { it.id == countryId }?.genitive }
            ?: getString(R.string.unknown_country)
        return "$genre $country"
    }

    private fun reloadData() {
        binding.retryBtn.visibility = View.GONE

        viewModel.fetchPremieres()
        viewModel.fetchTop250()
        viewModel.fetchFirstRandomMovies()
        viewModel.fetchSecondRandomMovies()
        viewModel.fetchTVSeries()
    }

    private fun navigateToListOfMovies(type: String, countryId: Int, genreId: Int) {
        val action = HomeFragmentDirections.actionHomeFragmentToListOfMoviesFragment(
            collectionName = "",
            type = type,
            countryId = countryId,
            genreId = genreId
        )
        findNavController().navigate(action)
    }

    private fun navigateToMovieDetails(kinopoiskId: Int) {
        val action = HomeFragmentDirections.actionHomeFragmentToMovieDetailsFragment(kinopoiskId)
        findNavController().navigate(action)
    }

    private fun showErrorBottomSheet(errorMessage: String) {
        val errorBottomSheet = ErrorBottomSheet.newInstance(errorMessage)
        errorBottomSheet.show(childFragmentManager, "ErrorBottomSheet")
    }
}