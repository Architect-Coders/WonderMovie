package com.brunix.wondermovie.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.brunix.wondermovie.model.Movie
import com.brunix.wondermovie.model.MoviesRepository
import com.brunix.wondermovie.ui.common.Scope
import kotlinx.coroutines.launch

class MovieListViewModel(private val moviesRepository: MoviesRepository) : ViewModel(),
    Scope by Scope.Impl() {

    sealed class UiModel {
        // UI state: the list of movies to show in the screen is loading
        object Loading : UiModel()
        // UI state: Show the list of movies in the screen
        class Content(val movies: List<Movie>) : UiModel()
        // UI state: Go to the detail of a movie
        class Navigation(val movie: Movie) : UiModel()
    }

    private val _model = MutableLiveData<UiModel>()
    val model: LiveData<UiModel>
        get() {
            if (_model.value == null) {
                refresh()
            }
            return _model
        }

    init {
        initScope()
    }

    private fun refresh() {
        launch {
            _model.value = UiModel.Loading
            _model.value = UiModel.Content(moviesRepository.findPopularMovies().results)
            // No need for a hideProgress: the view will be responsible for that (the view assumes that
            // whenever there is no info loading, the progress will not be shown)
        }
    }

    override fun onCleared() {
        destroyScope()
    }

    fun onMovieClicked(movie: Movie) {
        _model.value = UiModel.Navigation(movie)
    }
}

@Suppress("UNCHECKED_CAST")
class MovieListViewModelFactory(private val moviesRepository: MoviesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        MovieListViewModel(moviesRepository) as T
}
