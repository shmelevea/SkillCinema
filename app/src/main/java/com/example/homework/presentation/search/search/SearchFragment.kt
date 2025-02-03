package com.example.homework.presentation.search.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homework.R
import com.example.homework.data.remote.SearchFilter
import com.example.homework.databinding.FragmentSearchBinding
import com.example.homework.presentation.bottomSheet.error.ErrorBottomSheet
import com.example.homework.presentation.search.SearchFilterSharedViewModel
import com.example.homework.presentation.ui.decorations.BottomOffsetItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModels()
    private val filterViewModel: SearchFilterSharedViewModel by activityViewModels()
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            if (!viewModel.isErrorShown) {
                val errorMessage = getString(R.string.error_msg)
                showErrorBottomSheet(errorMessage)
                viewModel.isErrorShown = true
            }
        }

        lifecycleScope.launch {
            filterViewModel.typeMovie
                .combine(filterViewModel.sortOrder) { typeMovie, sortOrder ->
                    val filter = SearchFilter(
                        type = typeMovie,
                        order = sortOrder,
                        ratingFrom = filterViewModel.ratingRange.value.first,
                        ratingTo = filterViewModel.ratingRange.value.second,
                        yearFrom = filterViewModel.yearRange.value.first,
                        yearTo = filterViewModel.yearRange.value.second,
                        countryId = filterViewModel.selectedCountryId.value,
                        genreId = filterViewModel.selectedGenreId.value
                    )
                    Log.d("SearchFragment", "Updated Filter: $filter")
                    filter
                }.distinctUntilChanged()
                .collect { newFilter ->
                    viewModel.updateFilter(newFilter)
                }
        }

        searchAdapter = SearchAdapter { item ->
            findNavController().navigate(
                SearchFragmentDirections.actionSearchFragmentToMovieDetailsFragment(
                    item.kinopoiskId
                )
            )
        }

        lifecycleScope.launch {
            textChanges(binding.searchInputField.searchEditText)
                .collectLatest { query ->
                    viewModel.setSearchQuery(query)
                    if (query.isNotEmpty()) {
                        viewModel.searchMovies(query).collectLatest {
                            searchAdapter.submitData(it)
                        }
                    } else {
                        searchAdapter.submitData(PagingData.empty())
                        withContext(Dispatchers.Main) {
                            binding.notFoundTv.visibility = View.GONE
                        }
                    }
                }
        }

        binding.searchInputField.apply {
            ivSettingsBtn.visibility = View.VISIBLE
            divider.visibility = View.VISIBLE
            searchEditText.hint = getString(R.string.search_edit)
        }

        val bottomOffset = resources.getDimensionPixelSize(R.dimen.small_spacing)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
            addItemDecoration(BottomOffsetItemDecoration(bottomOffset))
        }

        binding.searchInputField.ivSettingsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_settingSearchFragment)
        }

        searchAdapter.addLoadStateListener { loadState ->
            val isListEmpty =
                loadState.source.refresh is LoadState.NotLoading && searchAdapter.itemCount == 0
            binding.recyclerView.isVisible = !isListEmpty
            binding.notFoundTv.isVisible =
                isListEmpty || loadState.source.refresh is LoadState.Error
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.searchQuery.value?.let { query ->
            if (query.isNotEmpty()) {
                lifecycleScope.launch {
                    viewModel.searchMovies(query).collectLatest {
                        searchAdapter.submitData(it)
                    }
                }
            }
        }
    }

    private fun textChanges(editText: EditText): Flow<String> = callbackFlow {
        val listener = editText.addTextChangedListener { text ->
            trySend(text.toString())
        }
        awaitClose { editText.removeTextChangedListener(listener) }
    }

    private fun showErrorBottomSheet(errorMessage: String) {
        val errorBottomSheet = ErrorBottomSheet.newInstance(errorMessage)
        errorBottomSheet.show(childFragmentManager, "ErrorBottomSheet")
    }
}