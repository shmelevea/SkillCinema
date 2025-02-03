package com.example.homework.presentation.search.countryGenre

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homework.R
import com.example.homework.data.remote.CountryGenreItem
import com.example.homework.databinding.FragmentCountryGenreBinding
import com.example.homework.presentation.bottomSheet.error.ErrorBottomSheet
import com.example.homework.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
    class CountryGenreFragment : Fragment() {

    private lateinit var binding: FragmentCountryGenreBinding
    private val viewModel: CountryGenreViewModel by viewModels()
    private lateinit var adapter: CountryGenreAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCountryGenreBinding.inflate(layoutInflater)
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

        val isCountryRequest = arguments?.getBoolean(ARG_IS_COUNTRY_REQUEST) ?: true

        binding.toolbar.apply {
            binding.toolbarTitle.text = if (isCountryRequest) {
                getString(R.string.country)
            } else {
                getString(R.string.genre)
            }
            setNavigationOnClickListener { findNavController().navigateUp() }
        }

        adapter = CountryGenreAdapter { id ->
            onItemSelected(id)
        }

        binding.catRv.layoutManager = LinearLayoutManager(requireContext())
        binding.catRv.adapter = adapter
        if (isCountryRequest) {
            viewModel.loadCountries()
            viewModel.countries.observe(viewLifecycleOwner) { countries ->
                adapter.submitList(countries.map { country ->
                    CountryGenreItem(country.id, country.country)
                })
            }
        } else {
            viewModel.loadGenres()
            viewModel.genres.observe(viewLifecycleOwner) { genres ->
                adapter.submitList(genres.map { genre ->
                    CountryGenreItem(genre.id, genre.genre)
                })
            }
        }

        binding.searchInputField.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filterList(s.toString(), isCountryRequest)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        viewModel.filteredItems.observe(viewLifecycleOwner) { filteredList ->
            adapter.submitList(filteredList)
        }
    }

    private fun onItemSelected(selectedId: Int) {
        val selectedItem = adapter.getCurrentList().find { it.id == selectedId }

        val result = Bundle().apply {
            putInt(ARG_SELECTED_ITEM_ID, selectedId)
            putString(ARG_SELECTED_ITEM, selectedItem?.name)
            putBoolean(ARG_IS_COUNTRY, arguments?.getBoolean(ARG_IS_COUNTRY_REQUEST) ?: true)
        }
        parentFragmentManager.setFragmentResult(RESULT_KEY_COUNTRY_GENRE, result)
        findNavController().popBackStack()
    }

    private fun showErrorBottomSheet(errorMessage: String) {
        val errorBottomSheet = ErrorBottomSheet.newInstance(errorMessage)
        errorBottomSheet.show(childFragmentManager, "ErrorBottomSheet")
    }
}