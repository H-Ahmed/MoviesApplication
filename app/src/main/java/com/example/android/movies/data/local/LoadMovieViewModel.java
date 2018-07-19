package com.example.android.movies.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.movies.models.Movie;

public class LoadMovieViewModel extends ViewModel {

    private LiveData<Movie> movie;

    public LoadMovieViewModel(AppDatabase database, int movieId) {
        movie = database.movieDeo().loadMovieById(movieId);
    }

    public LiveData<Movie> getMovie() {
        return movie;
    }
}
