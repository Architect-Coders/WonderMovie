package com.brunix.wondermovie.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.brunix.wondermovie.MyApplication
import com.brunix.wondermovie.R
import com.brunix.wondermovie.ui.common.app
import com.brunix.wondermovie.ui.common.getViewModel
import com.brunix.wondermovie.ui.common.loadUrl
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlinx.android.synthetic.main.movie_detail.*

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a [MovieListActivity]
 * in two-pane mode (on tablets) or a [MovieDetailActivity]
 * on handsets.
 */
class MovieDetailFragment : Fragment(){

    /**
     * The movie this fragment is presenting.
     */
    private var movieId: Int = 0

    private lateinit var component: MovieDetailActivityComponent
    private val viewModel by lazy { getViewModel { component.movieDetailViewModel } }

    private lateinit var myApp: MyApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_MOVIE)) {
                // Load the movie specified by the fragment
                movieId = it.getInt(ARG_MOVIE, -1)
            }
        }

        component = activity!!.app.component.plus(MovieDetailActivityModule(movieId))

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.movie_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.model.observe(this, Observer(::updateUi))

        activity?.movie_detail_favorite_btn?.setOnClickListener { viewModel.onFavoriteClicked() }
    }

    private fun updateUi(model: MovieDetailViewModel.UiModel) = with(model.movie) {
        activity?.toolbar_layout?.title = title
        activity?.movie_detail_image?.loadUrl("https://image.tmdb.org/t/p/w780$backdropPath")
        movie_detail_summary.text = overview
        movie_detail_info.setMovie(this)

        val favIcon = if (favorite) R.drawable.ic_favorite_on else R.drawable.ic_favorite_off
        activity?.movie_detail_favorite_btn!!.setImageDrawable(activity?.getDrawable(favIcon))
    }

    companion object {
        private const val ARG_MOVIE = "MovieDetailFragment:movie"

        /**
         * Create a new instance of MovieDetailFragment, initialized to
         * show the movie in 'movie'.
         */
        fun newInstance(movieId: Int?) = MovieDetailFragment().apply {
            // Supply movie input as an argument.
            arguments = bundleOf(ARG_MOVIE to movieId)
        }
    }
}
