package com.example.homework.presentation.loader

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.homework.R
import com.example.homework.databinding.FragmentLoaderBinding
import com.example.homework.presentation.basic.homepage.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoaderFragment : Fragment() {

    private val homeViewModel: HomeViewModel by activityViewModels()
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var binding: FragmentLoaderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoaderBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeDataLoading()
        startDataLoading()
        setupTimeout()
    }

    private fun startDataLoading() {
        homeViewModel.fetchPremieres()
        homeViewModel.fetchTop250()
        homeViewModel.fetchFirstRandomMovies()
        homeViewModel.fetchSecondRandomMovies()
        homeViewModel.fetchTVSeries()
    }

    private fun observeDataLoading() {
        homeViewModel.combinedLiveData.observe(viewLifecycleOwner) {
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({
                navigateToHomeFragment()
            }, 1_500)
        }
    }

    private fun setupTimeout() {
        handler.postDelayed({
            navigateToHomeFragment()
        }, 1_500)
    }

    private fun navigateToHomeFragment() {
        findNavController().navigate(
            R.id.action_loaderFragment_to_homeFragment
        )
    }
}