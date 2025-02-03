package com.example.homework.presentation.basic.movieDetails

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.homework.R
import com.example.homework.data.MovieCategory
import com.example.homework.databinding.FragmentMovieDetailsBinding
import com.example.homework.presentation.LocalCollectionsViewModel
import com.example.homework.presentation.bottomSheet.collection.CollectionFragment
import com.example.homework.presentation.bottomSheet.error.ErrorBottomSheet
import com.example.homework.presentation.ui.decorations.EndOffsetItemDecoration
import com.example.homework.presentation.ui.decorations.GridSpacingItemDecoration
import com.example.homework.utils.MAX_ACTORS_COUNT
import com.example.homework.utils.MAX_DESCRIPTION_LENGTH
import com.example.homework.utils.MAX_TEAM_COUNT
import com.example.homework.utils.formatFilmLength
import com.example.homework.utils.formatRatingAgeLimits
import com.example.homework.utils.trimText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {

    private lateinit var binding: FragmentMovieDetailsBinding
    private val movieDetailsViewModel: MovieDetailsViewModel by viewModels()
    private val localCollectionsViewModel: LocalCollectionsViewModel by activityViewModels()
    private lateinit var actorsAdapter: TeamAdapter
    private lateinit var teamAdapter: TeamAdapter
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var similarMoviesAdapter: SimilarMoviesAdapter
    private var isDescriptionExpanded = false
    private var fullDescription: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val movieId = arguments?.let { MovieDetailsFragmentArgs.fromBundle(it).kinopoiskId } ?: -1
        if (movieId != -1) {
            movieDetailsViewModel.init(movieId)
            localCollectionsViewModel.init(movieId)
        }

        actorsAdapter = TeamAdapter(true) { personId ->
            navigateToPersonFragment(personId)
        }

        teamAdapter = TeamAdapter(false) { personId ->
            navigateToPersonFragment(personId)
        }

        galleryAdapter = GalleryAdapter()

        similarMoviesAdapter = SimilarMoviesAdapter { kinopoiskId ->
            navigateToMovieDetails(kinopoiskId)
        }

        handleError()
        clickListener(movieId)
        setupShowAllButtons(movieId)
        observeViewModelData()
        setupAdapters()
        updateButtonState(movieId)

        binding.oneTv.setOnClickListener {
            updateButtonState(movieId)
        }
    }

    private fun updateButtonState(movieId: Int) {
        localCollectionsViewModel.isMovieInCategory(movieId, MovieCategory.Viewed).observe(viewLifecycleOwner) { isInCategory ->
            binding.eyeBtn.isSelected = isInCategory
        }

        localCollectionsViewModel.isMovieInCategory(movieId, MovieCategory.Liked).observe(viewLifecycleOwner) { isInCategory ->
            binding.likeBtn.isSelected = isInCategory
        }

        localCollectionsViewModel.isMovieInCategory(movieId, MovieCategory.Wishlist).observe(viewLifecycleOwner) { isInCategory ->
            binding.bookmarkBtn.isSelected = isInCategory
        }
    }

    private fun clickListener(movieId: Int) {
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.shareBtn.setOnClickListener {
            sharedUrl()
        }

        binding.eyeBtn.setOnClickListener {
            movieDetailsViewModel.filmDetails.value?.let { film ->
                localCollectionsViewModel.toggleMovieInCategory(film, MovieCategory.Viewed) {
                    updateButtonState(movieId)
                }
            }
        }

        binding.likeBtn.setOnClickListener {
            movieDetailsViewModel.filmDetails.value?.let { film ->
                localCollectionsViewModel.toggleMovieInCategory(film, MovieCategory.Liked) {
                    updateButtonState(movieId)
                }
            }
        }

        binding.bookmarkBtn.setOnClickListener {
            movieDetailsViewModel.filmDetails.value?.let { film ->
                localCollectionsViewModel.toggleMovieInCategory(film, MovieCategory.Wishlist) {
                    updateButtonState(movieId)
                }
            }
        }

        binding.similarInclude.apply {
            val clickListener = View.OnClickListener {
                navigateToSimilarListFragment()
            }
            sumAllTv.setOnClickListener(clickListener)
            showAllBtn.setOnClickListener(clickListener)
        }

        binding.otherBtn.setOnClickListener {
            navigateToCollectionFragment(movieId)
        }
    }

    private fun handleError() {
        movieDetailsViewModel.errorLiveData.observe(viewLifecycleOwner) {
            if (!movieDetailsViewModel.isErrorShown) {
                val errorMessage = getString(R.string.error_msg)
                showErrorBottomSheet(errorMessage)
                movieDetailsViewModel.isErrorShown = true
            }
        }
    }


    private fun sharedUrl() {
        val movieUrl = movieDetailsViewModel.filmDetails.value?.imdbId?.takeIf { it.isNotEmpty() }?.let {
            "https://www.imdb.com/title/$it/"
        }

        movieUrl?.let {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, it)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
        } ?: Toast.makeText(context, R.string.no_data, Toast.LENGTH_SHORT).show()
    }

    private fun observeViewModelData() {
        movieDetailsViewModel.filmDetails.observe(viewLifecycleOwner) { film ->
            val ratingKp = film.ratingKinopoisk ?: ""
            binding.oneTv.text = getString(
                R.string.rating_with_name,
                ratingKp.toString(),
                film.nameRu ?: film.nameOriginal
            )

            binding.twoTv.text = getString(
                R.string.year_with_genres,
                film.year,
                film.genres.joinToString(", ") { it.genre }
            )
            val formattedRating = film.ratingAgeLimits?.let { formatRatingAgeLimits(it) }
                ?: ("- " + getString(R.string.age_label))
            val formattedLength = film.filmLength?.let { formatFilmLength(it, requireContext()) }
                ?: ("- " + getString(R.string.minutes_label))

            binding.threeTv.text = getString(
                R.string.country_length_age,
                film.countries.firstOrNull()?.country
                    ?: getString(R.string.unknown_country),
                formattedLength,
                formattedRating
            )

            binding.shortDescriptionTv.text =
                film.shortDescription ?: getString(R.string.description_not_available)
            fullDescription = film.description ?: getString(R.string.description_not_available)

            binding.descriptionTv.text =
                trimText(fullDescription, MAX_DESCRIPTION_LENGTH)

            Glide.with(this)
                .load(film.posterUrl)
                .into(binding.posterIv)

            val seasonSection = binding.seasonSection.root

            if (film.serial) {
                seasonSection.visibility = View.VISIBLE
                movieDetailsViewModel.loadSeasons(film.kinopoiskId)
            } else {
                seasonSection.visibility = View.GONE
            }
        }

        movieDetailsViewModel.teamData.observe(viewLifecycleOwner) { teamList ->
            val actors = teamList.filter { it.professionKey == "ACTOR" }
            val nonActors = teamList.filter { it.professionKey != "ACTOR" }

            actorsAdapter.updateList(actors.take(MAX_ACTORS_COUNT))
            teamAdapter.updateList(nonActors.take(MAX_TEAM_COUNT))

            binding.actorInclude.sumAllTv.text = actors.size.toString()
            binding.teamInclude.sumAllTv.text = nonActors.size.toString()
        }

        movieDetailsViewModel.galleryData.observe(viewLifecycleOwner) { gallery ->
            galleryAdapter.updateGalleryList(gallery.take(20))
        }

        movieDetailsViewModel.totalPhotoCount.observe(viewLifecycleOwner) { count ->
            binding.galleryInclude.sumAllTv.text = count.toString()
        }

        movieDetailsViewModel.similarMoviesData.observe(viewLifecycleOwner) { similarMovies ->
            similarMoviesAdapter.updateSimilarMoviesList(similarMovies.take(20))
            binding.similarInclude.sumAllTv.text = similarMovies.size.toString()
        }

        movieDetailsViewModel.similarMoviesData.observe(viewLifecycleOwner) { similarMovies ->
            similarMoviesAdapter.updateSimilarMoviesList(similarMovies.take(20))
            movieDetailsViewModel.loadDetailedSimilarMovies(similarMovies)
        }

        movieDetailsViewModel.detailedSimilarMovies.observe(viewLifecycleOwner) { detailedMovies ->
            similarMoviesAdapter.updateDetailedMoviesList(detailedMovies)
        }

        binding.descriptionTv.setOnClickListener {
            toggleDescription()
        }

        movieDetailsViewModel.seasonsData.observe(viewLifecycleOwner) { seasonsResponse ->
            val seasonsCount = seasonsResponse.items.size
            val episodesCount = seasonsResponse.items.sumOf { it.episodes.size }
            val seasonText = resources.getQuantityString(
                R.plurals.season_count,
                seasonsCount,
                seasonsCount
            )
            val episodeText = resources.getQuantityString(
                R.plurals.episode_count,
                episodesCount,
                episodesCount
            )
            val text = "$seasonText, $episodeText"
            binding.seasonSection.sumTv.text = text
        }
    }

    private fun toggleDescription() {
        isDescriptionExpanded = !isDescriptionExpanded
        binding.descriptionTv.text = if (isDescriptionExpanded) fullDescription else trimText(
            fullDescription,
            MAX_DESCRIPTION_LENGTH
        )
    }

    private fun setupAdapters() {
        val bottomSpacing = resources.getDimensionPixelSize(R.dimen.small_spacing)
        val spacingHorizontal = resources.getDimensionPixelSize(R.dimen.small_spacing)

        binding.actorInclude.sectionTitleTv.text = getString(R.string.actors)
        binding.actorInclude.sectionRv.apply {
            adapter = actorsAdapter
            layoutManager = GridLayoutManager(context, 4, GridLayoutManager.HORIZONTAL, false)
            addItemDecoration(GridSpacingItemDecoration(4, spacingHorizontal, bottomSpacing, GridLayoutManager.HORIZONTAL))
        }

        binding.teamInclude.sectionTitleTv.text = getString(R.string.cast)
        binding.teamInclude.sectionRv.apply {
            adapter = teamAdapter
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
            addItemDecoration(GridSpacingItemDecoration(2, spacingHorizontal, bottomSpacing, GridLayoutManager.HORIZONTAL))
        }

        binding.galleryInclude.sectionTitleTv.text = getString(R.string.gallery)
        binding.galleryInclude.sectionRv.apply {
            adapter = galleryAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(EndOffsetItemDecoration(spacingHorizontal))
        }

        binding.similarInclude.sectionTitleTv.text = getString(R.string.similar_movies)
        binding.similarInclude.sectionRv.apply {
            adapter = similarMoviesAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(EndOffsetItemDecoration(spacingHorizontal))
        }
    }

    private fun setupShowAllButtons(movieId: Int) {
        binding.actorInclude.apply {
            val actorClickListener = View.OnClickListener {
                navigateToTeamListFragment(movieId, true)
            }
            sumAllTv.setOnClickListener(actorClickListener)
            showAllBtn.setOnClickListener(actorClickListener)
        }

        binding.teamInclude.apply {
            val teamClickListener = View.OnClickListener {
                navigateToTeamListFragment(movieId, false)
            }
            sumAllTv.setOnClickListener(teamClickListener)
            showAllBtn.setOnClickListener(teamClickListener)
        }
        binding.galleryInclude.apply {
            val galleryClickListener = View.OnClickListener {
                navigateToGalleryFragment(movieId)
            }
            sumAllTv.setOnClickListener(galleryClickListener)
            showAllBtn.setOnClickListener(galleryClickListener)
        }
        binding.seasonSection.allTv.setOnClickListener {
            navigateToSeriesFragment(movieId)
        }
    }

    private fun navigateToPersonFragment(personId: Int) {
        val action =
            MovieDetailsFragmentDirections.actionMovieDetailsFragmentToPersonFragment(personId)
        findNavController().navigate(action)
    }

    private fun navigateToSimilarListFragment() {
        val movieId = arguments?.let { MovieDetailsFragmentArgs.fromBundle(it).kinopoiskId } ?: -1
        if (movieId != -1) {
            val action =
                MovieDetailsFragmentDirections.actionMovieDetailsFragmentToSimilarListFragment(
                    movieId
                )
            findNavController().navigate(action)
        }
    }

    private fun navigateToTeamListFragment(movieId: Int, isActors: Boolean) {
        val action = MovieDetailsFragmentDirections
            .actionMovieDetailsFragmentToTeamListFragment(movieId, isActors)
        findNavController().navigate(action)
    }

    private fun navigateToMovieDetails(movieId: Int) {
        val action = MovieDetailsFragmentDirections.actionMovieDetailsFragmentSelf(movieId)
        findNavController().navigate(action)
    }

    private fun navigateToGalleryFragment(movieId: Int) {
        val action =
            MovieDetailsFragmentDirections.actionMovieDetailsFragmentToGalleryFragment(movieId)
        findNavController().navigate(action)

    }

    private fun navigateToSeriesFragment(movieId: Int) {
        val action =
            MovieDetailsFragmentDirections.actionMovieDetailsFragmentToSeriesFragment(movieId)
        findNavController().navigate(action)
    }

    private fun showErrorBottomSheet(errorMessage: String) {
        val errorBottomSheet = ErrorBottomSheet.newInstance(errorMessage)
        errorBottomSheet.show(childFragmentManager, "ErrorBottomSheet")
    }

    private fun navigateToCollectionFragment(movieId: Int) {
        val collectionFragment = CollectionFragment()
        collectionFragment.setOnDismissListener {
            updateButtonState(movieId)
        }

        val bundle = Bundle().apply {
            putInt("movieId", movieId)
        }
        collectionFragment.arguments = bundle
        collectionFragment.show(parentFragmentManager, "CollectionFragment")
    }
}
