package com.example.homework.presentation

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.homework.R
import com.example.homework.databinding.FragmentBasicBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BasicFragment : Fragment() {

    private lateinit var binding: FragmentBasicBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBasicBinding.inflate(layoutInflater)
        val view = binding.root

        val navView: BottomNavigationView = binding.navView

        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> navView.menu.findItem(R.id.homeFragment).isChecked = true
                R.id.searchFragment -> navView.menu.findItem(R.id.searchFragment).isChecked = true
                R.id.profileFragment -> navView.menu.findItem(R.id.profileFragment).isChecked = true
                else -> {
                    for (i in 0 until navView.menu.size()) {
                        navView.menu.getItem(i).isChecked = false
                    }
                }
            }

            if (
                destination.id == R.id.loaderFragment ||
                destination.id == R.id.settingSearchFragment ||
                destination.id == R.id.fullScreenImageFragment ||
                destination.id == R.id.countryGenreFragment ||
                destination.id == R.id.yearSelectorFragment
            ) {
                navView.visibility = View.GONE
                binding.shadowContainer.visibility = View.GONE
            } else {
                navView.visibility = View.VISIBLE
                binding.shadowContainer.visibility = View.VISIBLE
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val navController = binding.navHostFragment.findNavController()
                    if (navController.currentDestination?.id == R.id.homeFragment) {
                        requireActivity().finish()
                    } else {
                        navController.navigateUp()
                    }
                }
            }
        )
    }
}