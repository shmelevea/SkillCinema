package com.example.homework.presentation.bottomSheet.collection

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.homework.data.MovieCategory
import com.example.homework.data.remote.response.InfoById
import com.example.homework.data.toMovieCategory
import com.example.homework.databinding.FragmentCollectionBinding
import com.example.homework.presentation.LocalCollectionsViewModel
import com.example.homework.presentation.basic.movieDetails.MovieDetailsViewModel
import com.example.homework.presentation.notify.NotifyFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CollectionFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCollectionBinding
    private val movieDetailsViewModel: MovieDetailsViewModel by viewModels()
    private val localCollectionsViewModel: LocalCollectionsViewModel by activityViewModels()
    private lateinit var adapter: CollectionAdapter
    private var onDismissListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movieId = arguments?.getInt("movieId") ?: return

        movieDetailsViewModel.filmDetails.observe(viewLifecycleOwner) { filmDetails ->
            filmDetails?.let { bindFilmDetails(it) }
        }

        if (movieDetailsViewModel.filmDetails.value == null) {
            movieDetailsViewModel.init(movieId)
        }

        localCollectionsViewModel.observeMovieCategoryStatus(movieId)
        localCollectionsViewModel.loadCategories()

        localCollectionsViewModel.categoryData.observe(viewLifecycleOwner) { categories ->
            val filteredCategories = categories.filter { categoryItem ->
                categoryItem.category.name != MovieCategory.Interested.name
            }

            val updatedCategories = filteredCategories.toMutableList()

            val movieCategoryStatusMap = mutableMapOf<String, Boolean>()
            updatedCategories.forEach { categoryItem ->
                localCollectionsViewModel.isMovieInCategory(
                    movieId,
                    categoryItem.category.toMovieCategory()
                )
                    .observe(viewLifecycleOwner) { isInCategory ->
                        movieCategoryStatusMap[categoryItem.category.name] = isInCategory
                        if (::adapter.isInitialized) {
                            adapter.updateCollections(updatedCategories, movieCategoryStatusMap)
                        }
                    }
            }

            if (!::adapter.isInitialized) {
                adapter = CollectionAdapter(
                    collections = updatedCategories,
                    movieCategoryStatusMap = movieCategoryStatusMap,
                    onCollectionClicked = { categoryItem, _ ->
                        movieDetailsViewModel.filmDetails.value?.let { movie ->
                            localCollectionsViewModel.toggleMovieInCategory(
                                movie,
                                categoryItem.category.toMovieCategory()
                            ) {
                            }
                        }
                    },
                    onCreateNewCollectionClicked = { createNewCollection() }
                )
                binding.collectionRv.adapter = adapter
                binding.collectionRv.layoutManager = LinearLayoutManager(requireContext())
            }
        }
        binding.closeBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun bindFilmDetails(details: InfoById) {
        with(binding.include) {
            Glide.with(this@CollectionFragment)
                .load(details.posterUrlPreview)
                .into(posterIv)
            if (details.ratingKinopoisk != null) {
                ratingBtn.apply {
                    text = details.ratingKinopoisk.toString()
                    visibility = View.VISIBLE
                }
            }
            titleTv.text = details.nameRu ?: details.nameOriginal
            val genres = details.genres.joinToString(", ") { it.genre }
            val text = "${details.year}, $genres"
            genreTv.text = text
        }
    }

    private fun createNewCollection() {
        val notifyFragment = NotifyFragment()
        notifyFragment.show(parentFragmentManager, "NotifyFragment")

        notifyFragment.setOnDismissListener {
            localCollectionsViewModel.loadCategories()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.invoke()
    }

    fun setOnDismissListener(listener: () -> Unit) {
        onDismissListener = listener
    }
}