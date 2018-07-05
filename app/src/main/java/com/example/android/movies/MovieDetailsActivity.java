package com.example.android.movies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movies.models.Movie;
import com.example.android.movies.utilities.JsonUtils;
import com.example.android.movies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Movie>{

    @BindView(R.id.iv_poster_movie)
    ImageView mPosterMovieImageView;
    @BindView(R.id.tv_original_title)
    TextView mOriginalTitleTextView;
    @BindView(R.id.tv_overview)
    TextView mOverviewTextView;
    @BindView(R.id.tv_vote_average)
    TextView mVoteAverageTextView;
    @BindView(R.id.tv_release_date)
    TextView mReleaseDateTextView;
    @BindView(R.id.pb_movie)
    ProgressBar mMovieProgressBar;
    @BindView(R.id.ly_movie_data)
    LinearLayout mMovieDataLinearLayout;
    @BindView(R.id.detail_error_message)
    TextView mDetailErrorMessageTextView;

    private static String API_KEY_VALUE;
    private static final String Movie_ID = "movie_id";
    private static final int MOVIE_LOADER_ID = 52;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        API_KEY_VALUE = getResources().getString(R.string.api_key);

        Intent intent = getIntent();
        if (intent == null) {
            closeActivity();
        }

        if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            String movieId = intent.getStringExtra(Intent.EXTRA_TEXT);
            Bundle movieBundle = new Bundle();
            movieBundle.putString(Movie_ID, movieId);

            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<Movie> movieLoader = loaderManager.getLoader(MOVIE_LOADER_ID);
            if (movieLoader == null){
                loaderManager.initLoader(MOVIE_LOADER_ID, movieBundle, this);
            }else {
                loaderManager.restartLoader(MOVIE_LOADER_ID, movieBundle, this);
            }
        } else {
            closeActivity();
        }
    }

    private void closeActivity() {
        finish();
        Toast.makeText(this, getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public Loader<Movie> onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<Movie>(this) {
            @Override
            protected void onStartLoading() {
                if (args == null || !args.containsKey(Movie_ID)) {
                    closeActivity();
                    return;
                }
                mMovieProgressBar.setVisibility(View.VISIBLE);
                mMovieDataLinearLayout.setVisibility(View.GONE);
                forceLoad();
            }

            @Nullable
            @Override
            public Movie loadInBackground() {
                String idValue = args.getString(Movie_ID);
                URL moviesRequestURL = NetworkUtils.buildUrl(API_KEY_VALUE, idValue);
                try {
                    String jsonMovieData = NetworkUtils.getResponseFromHttpUrl(moviesRequestURL);
                    return JsonUtils.parseMovieDataFromJson(jsonMovieData);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Movie> loader, Movie movieData) {
        if (movieData != null) {
            showMovieDetailData(movieData);
        } else {
            showDetailErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Movie> loader) {

    }

    private void showMovieDetailData(Movie movieData) {
        mMovieProgressBar.setVisibility(View.GONE);
        mMovieDataLinearLayout.setVisibility(View.VISIBLE);
        Picasso.get().load("http://image.tmdb.org/t/p/w500//" + movieData.getPosterPath()).into(mPosterMovieImageView);
        mOriginalTitleTextView.setText(movieData.getOriginalTitle());
        mOverviewTextView.setText(movieData.getOverView());
        mVoteAverageTextView.setText(movieData.getVoteAverage());
        mReleaseDateTextView.setText(movieData.getReleaseDate());
    }

    private void showDetailErrorMessage() {
        mPosterMovieImageView.setVisibility(View.GONE);
        mMovieProgressBar.setVisibility(View.GONE);
        mMovieDataLinearLayout.setVisibility(View.GONE);
        mDetailErrorMessageTextView.setVisibility(View.VISIBLE);
    }
}
