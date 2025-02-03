package com.example.homework.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homework.R
import com.example.homework.data.ApiKeyProvider
import com.example.homework.entity.CategoryItem
import com.example.homework.data.MovieCategory
import com.example.homework.data.mapper.toMovieEntity
import com.example.homework.databinding.FragmentProfileBinding
import com.example.homework.presentation.LocalCollectionsViewModel
import com.example.homework.presentation.bottomSheet.error.ErrorBottomSheet
import com.example.homework.presentation.notify.NotifyFragment
import com.example.homework.presentation.ui.decorations.EndOffsetItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    @Inject
    lateinit var apiKeyProvider: ApiKeyProvider
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: LocalCollectionsViewModel by viewModels()
    private lateinit var interestedMoviesAdapter: ProfileMoviesAdapter
    private lateinit var viewedMoviesAdapter: ProfileMoviesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCollectionsViewPager()
        setupMoviesAdapters()
        viewModel.loadCategories()
        viewModel.loadMovies()
        observeViewModel()
        handleError()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        val clickListener = View.OnClickListener {
            createNewCollection()
        }

        binding.apply {
            createTv.setOnClickListener(clickListener)
            plusBtn.setOnClickListener(clickListener)
        }

        binding.keyBtn.setOnClickListener {
            apiKeyProvider.switchKey()
            val currentKey = apiKeyProvider.apiKey
            Toast.makeText(requireContext(), "Current Key: $currentKey", Toast.LENGTH_SHORT).show()
        }
    }

    private fun paginateCategories(categories: List<CategoryItem>): List<List<CategoryItem>> {
        val pageSize = 4
        return categories.chunked(pageSize)
    }

    private fun setupCollectionsViewPager() {
        viewModel.categoryData.observe(viewLifecycleOwner) { categoryItems ->
            val filteredCategories = categoryItems.filter { categoryItem ->
                categoryItem.category.name != MovieCategory.Interested.name &&
                        categoryItem.category.name != MovieCategory.Viewed.name
            }
            val pages = paginateCategories(filteredCategories)
            val pagerAdapter = CollectionPagerAdapter(
                pages,
                onMovieClick = { collectionName ->
                    navigateToListOfMovies(collectionName)
                },
                onCategoryRemoved = { categoryItem ->
                    viewModel.removeCategory(categoryItem)
                }
            )

            binding.collectionVp.adapter = pagerAdapter
        }
    }

    private fun setupMoviesAdapters() {
        val spacingHorizontal = resources.getDimensionPixelSize(R.dimen.small_spacing)

        interestedMoviesAdapter = ProfileMoviesAdapter(
            categoryName = MovieCategory.Interested.name,
            onMovieClick = { movieId -> navigateToMovieDetails(movieId) },
            onClearCollectionClick = { categoryItem ->
                viewModel.removeCategory(categoryItem)
            }
        )
        binding.interestedInclude.sectionRv.apply {
            adapter = interestedMoviesAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(EndOffsetItemDecoration(spacingHorizontal))
        }

        viewedMoviesAdapter = ProfileMoviesAdapter(
            categoryName = MovieCategory.Viewed.name,
            onMovieClick = { movieId -> navigateToMovieDetails(movieId) },
            onClearCollectionClick = { categoryItem ->
                viewModel.removeCategory(categoryItem)
            }
        )
        binding.viewedItemInclude.sectionRv.apply {
            adapter = viewedMoviesAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(EndOffsetItemDecoration(spacingHorizontal))
        }

        binding.interestedInclude.apply {
            val clickListener = View.OnClickListener {
                navigateToListOfMovies(MovieCategory.Interested.name)
            }
            sectionTitleTv.text = getString(R.string.interested)
            showAllBtn.setOnClickListener(clickListener)
            sumAllTv.setOnClickListener(clickListener)
        }

        binding.viewedItemInclude.apply {
            val clickListener = View.OnClickListener {
                navigateToListOfMovies(MovieCategory.Viewed.name)
            }
            sectionTitleTv.text = getString(R.string.viewed)
            showAllBtn.setOnClickListener(clickListener)
            sumAllTv.setOnClickListener(clickListener)
        }
    }

    private fun observeViewModel() {
        viewModel.interestedMovies.observe(viewLifecycleOwner) { movies ->
            interestedMoviesAdapter.updateMoviesList(movies.take(20))
        }

        viewModel.viewedMovies.observe(viewLifecycleOwner) { movies ->
            viewedMoviesAdapter.updateMoviesList(movies.take(20))
        }

        viewModel.interestedMoviesCount.observe(viewLifecycleOwner) { count ->
            binding.interestedInclude.sumAllTv.text = count.toString()
        }

        viewModel.viewedMoviesCount.observe(viewLifecycleOwner) { count ->
            binding.viewedItemInclude.sumAllTv.text = count.toString()
        }

        viewModel.detailedMovies.observe(viewLifecycleOwner) { detailedMovies ->
            val interestedDetailedMovies = detailedMovies.filter { movie ->
                viewModel.interestedMovies.value?.any { it.kinopoiskId == movie.kinopoiskId } == true
            }.take(20)

            val viewedDetailedMovies = detailedMovies.filter { movie ->
                viewModel.viewedMovies.value?.any { it.kinopoiskId == movie.kinopoiskId } == true
            }.take(20)

            interestedMoviesAdapter.updateMoviesList(interestedDetailedMovies.map { it.toMovieEntity() })
            viewedMoviesAdapter.updateMoviesList(viewedDetailedMovies.map { it.toMovieEntity() })
        }
    }

    private fun navigateToMovieDetails(movieId: Int) {
        val action =
            ProfileFragmentDirections.actionProfileFragmentToMovieDetailsFragment(
                movieId
            )
        findNavController().navigate(action)
    }

    private fun navigateToListOfMovies(collectionName: String) {
        val action = ProfileFragmentDirections
            .actionProfileFragmentToListOfMoviesFragment(
                collectionName = collectionName,
                type = "",
                countryId = 0,
                genreId = 0
            )
        findNavController().navigate(action)
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

    private fun showErrorBottomSheet(errorMessage: String) {
        val errorBottomSheet = ErrorBottomSheet.newInstance(errorMessage)
        errorBottomSheet.show(childFragmentManager, "ErrorBottomSheet")
    }

    private fun createNewCollection() {
        val notifyFragment = NotifyFragment()
        notifyFragment.show(parentFragmentManager, "NotifyFragment")

        notifyFragment.setOnDismissListener {
            viewModel.loadCategories()
        }
    }
}