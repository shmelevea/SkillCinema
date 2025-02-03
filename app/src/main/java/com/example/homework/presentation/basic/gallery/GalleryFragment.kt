package com.example.homework.presentation.basic.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.homework.R
import com.example.homework.databinding.FragmentUniversalPageBinding
import com.example.homework.presentation.bottomSheet.error.ErrorBottomSheet
import com.example.homework.presentation.ui.decorations.GalleryItemDecoration
import com.example.homework.utils.getNameCategory
import com.example.homework.utils.getStyledText
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment : Fragment() {

    private val viewModel: GalleryViewModel by viewModels()
    private lateinit var binding: FragmentUniversalPageBinding
    private lateinit var galleryAdapter: GalleryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUniversalPageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.toolbar.apply {
            binding.toolbarTitle.text = getString(R.string.gallery)
            setNavigationOnClickListener { findNavController().navigateUp() }
        }

        viewModel.availableCategories.observe(viewLifecycleOwner) {
            setupChipGroup()
        }

        setupPhotosAdapter()
        loadGalleryForMovie()
        handleError()
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

    private fun loadGalleryForMovie() {
        val movieId = GalleryFragmentArgs.fromBundle(requireArguments()).kinopoiskId
        viewModel.loadAvailableCategories(movieId)

        viewModel.selectedCategory.observe(viewLifecycleOwner) { category ->
            viewModel.getPhotosByCategory(movieId, category)
                .observe(viewLifecycleOwner) { pagingData ->
                    galleryAdapter.submitData(lifecycle, pagingData)
                }
        }
    }

    private fun setupChipGroup() {
        binding.categoryCg.apply {
            visibility = View.VISIBLE
            removeAllViews()

            viewModel.availableCategories.observe(viewLifecycleOwner) { availableCategories ->
                viewModel.selectedCategory.observe(viewLifecycleOwner) { selectedCategory ->
                    removeAllViews()

                    availableCategories.forEach { (category, count) ->
                        val chip = Chip(requireContext()).apply {
                            text = getStyledText(requireContext(), getGalleryCategory(category), count)

                            isCheckable = true
                            isChecked = (category == selectedCategory)

                            setOnClickListener {
                                viewModel.setSelectedCategory(category)
                            }
                        }
                        addView(chip)
                    }
                }
            }
        }
    }

    private fun setupPhotosAdapter() {
        galleryAdapter = GalleryAdapter{
                imageUrl ->
            navigateToFullScreenImage(imageUrl)
        }

        val spacing = resources.getDimensionPixelSize(R.dimen.large_spacing)

        binding.contentRv.apply {
            layoutManager = GridLayoutManager(context, 2).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if ((position + 1) % 3 == 0) 2 else 1
                    }
                }
            }
            adapter = galleryAdapter
            addItemDecoration(GalleryItemDecoration(spacing))
        }
    }

    private fun navigateToFullScreenImage(imageUrl: String) {
        val action = GalleryFragmentDirections.actionGalleryFragmentToFullScreenImageFragment(imageUrl)
        findNavController().navigate(action)
    }

    private fun getGalleryCategory(category: String): String {
        return getNameCategory(requireContext(), category)
    }

    private fun showErrorBottomSheet(errorMessage: String) {
        val errorBottomSheet = ErrorBottomSheet.newInstance(errorMessage)
        errorBottomSheet.show(childFragmentManager, "ErrorBottomSheet")
    }
}