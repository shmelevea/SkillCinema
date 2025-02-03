package com.example.homework.presentation.basic.team

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homework.R
import com.example.homework.databinding.FragmentListOfMoviesBinding
import com.example.homework.presentation.bottomSheet.error.ErrorBottomSheet
import com.example.homework.presentation.ui.decorations.BottomOffsetItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamListFragment : Fragment() {

    private val viewModel: TeamListViewModel by viewModels()
    private lateinit var binding: FragmentListOfMoviesBinding
    private lateinit var teamAdapter: TeamListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListOfMoviesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleError()

        val args = TeamListFragmentArgs.fromBundle(requireArguments())
        val movieId = args.kinopoiskId
        val isActorsList = args.isActors

        binding.toolbar.apply {
            binding.toolbarTitle.text =
                getString(if (isActorsList) R.string.actors else R.string.cast)
            setNavigationOnClickListener { findNavController().navigateUp() }
        }

        teamAdapter = TeamListAdapter { personId ->
            navigateToPersonFragment(personId)
        }

        viewModel.loadTeamData(movieId, isActorsList)

        viewModel.teamData.observe(viewLifecycleOwner) { teamList ->
            teamAdapter.updateList(teamList)
        }

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.allMovesRv.layoutManager = linearLayoutManager

        val bottomOffset = resources.getDimensionPixelSize(R.dimen.small_spacing)
        binding.allMovesRv.addItemDecoration(BottomOffsetItemDecoration(bottomOffset))

        binding.allMovesRv.adapter = teamAdapter
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

    private fun navigateToPersonFragment(personId: Int) {
        val action = TeamListFragmentDirections.actionTeamListFragmentToPersonFragment(personId)
        findNavController().navigate(action)
    }

    private fun showErrorBottomSheet(errorMessage: String) {
        val errorBottomSheet = ErrorBottomSheet.newInstance(errorMessage)
        errorBottomSheet.show(childFragmentManager, "ErrorBottomSheet")
    }
}