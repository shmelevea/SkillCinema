package com.example.homework.presentation.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.example.homework.R
import com.example.homework.databinding.FragmentOnboardingBinding
import androidx.viewpager2.widget.ViewPager2
import com.example.homework.presentation.BasicFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFragment : Fragment() {

    private lateinit var binding: FragmentOnboardingBinding
    private lateinit var adapter: OnboardingAdapter
    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        val view = binding.root

        val viewPager = binding.onboardingVp2
        adapter = OnboardingAdapter(this)
        viewPager.adapter = adapter

        val seekBar = binding.seekBar
        seekBar.max = adapter.itemCount - 1

        binding.skipBtn.setOnClickListener {
            val sharedPreferences =
                requireActivity().getSharedPreferences("app_prefs", AppCompatActivity.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply()

            val basicFragment = BasicFragment()

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, basicFragment)
                .addToBackStack(null)
                .commit()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewPager.setCurrentItem(progress, true)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                seekBar.progress = position
            }
        })

        return view
    }

    fun onPageChanged(position: Int) {
        binding.seekBar.progress = position
        viewModel.setCurrentPagePosition(position)
    }
}

