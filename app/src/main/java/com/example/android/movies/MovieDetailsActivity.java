package com.example.android.movies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movies.data.models.Movie;

import com.example.android.movies.data.models.Review;
import com.example.android.movies.data.models.Trailer;
import com.example.android.movies.data.local.AppDatabase;
import com.example.android.movies.data.local.AppExecutors;
import com.example.android.movies.data.remote.JsonUtils;
import com.example.android.movies.data.local.LoadMovieViewModel;
import com.example.android.movies.data.local.LoadMovieViewModelFactory;
import com.example.android.movies.data.remote.NetworkUtils;
import com.example.android.movies.ui.TrailersAdapter;
import com.example.android.movies.ui.ReviewsAdapter;
import com.squareup.picasso.Picasso;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity implements TrailersAdapter.TrailersAdapterOnClickHandler {

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
    @BindView(R.id.tv_run_time)
    TextView mRunTimeTextView;
    @BindView(R.id.tv_trailer_error_message)
    TextView mTrailerErrorMessage;
    @BindView(R.id.tv_review_error_message)
    TextView mReviewErrorMessage;
    @BindView(R.id.rv_movie_trailers)
    RecyclerView mTrailersRecyclerView;
    @BindView(R.id.rv_movie_reviews)
    RecyclerView mReviewsRecyclerView;
    @BindView(R.id.bt_favorite)
    ImageButton mFavoriteButton;

    private static final String TAG = "MovieDetailsActivity";

    private static String API_KEY_VALUE = BuildConfig.ApiKey;
    private static final String MOVIE_ID = "movie_id";

    private static final int MOVIE_LOADER_ID = 52;
    private static final int TRAILER_LOADER_ID = 53;
    private static final int REVIEW_LOADER_ID = 54;

    private static final String IMAGE_URL = "http://image.tmdb.org/t/p/w500//";
    private static final String YOUTUBE_APP_URL = "vnd.youtube:";
    private static final String YOUTUBE_URL = "http://www.youtube.com/watch?v=";

    private static final String TRAILER = "videos";
    private static final String REVIEWS = "reviews";


    private TrailersAdapter mTrailerAdapter;
    private ReviewsAdapter mReviewsAdapter;

    private LoaderManager loaderManager;
    private int movieId;

    private String firstVideoKey;

    private AppDatabase mDb;

    private boolean isDatabaseMember;

    private static final String MOVIE_DATA = "movie";

    private Movie mMovie;


    private LoaderManager.LoaderCallbacks<Movie> movieLoaderCallbacks
            = new LoaderManager.LoaderCallbacks<Movie>() {

        @NonNull
        @Override
        public Loader<Movie> onCreateLoader(int id, @Nullable final Bundle args) {
            return new AsyncTaskLoader<Movie>(MovieDetailsActivity.this) {
                @Override
                protected void onStartLoading() {
                    if (args == null || !args.containsKey(MOVIE_ID)) {
                        closeActivity();
                        return;
                    }
                    mMovieProgressBar.setVisibility(View.VISIBLE);
                    mMovieDataLinearLayout.setVisibility(View.GONE);
                    if (mMovie != null) {
                        deliverResult(mMovie);
                    } else {
                        forceLoad();
                    }
                }

                @Nullable
                @Override
                public Movie loadInBackground() {
                    int idValue = args.getInt(MOVIE_ID);
                    URL moviesRequestURL = NetworkUtils.buildUrl(API_KEY_VALUE, String.valueOf(idValue));
                    try {
                        String jsonMovieData = NetworkUtils.getResponseFromHttpUrl(moviesRequestURL);
                        return JsonUtils.parseMovieDataFromJson(jsonMovieData);
                    } catch (Exception e) {
                        Log.e(TAG, "loadInBackground: ", e);
                        return null;
                    }
                }

                @Override
                public void deliverResult(@Nullable Movie data) {
                    mMovie = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Movie> loader, Movie movieData) {
            if (movieData != null) {
                mMovie = movieData;
                showMovieDetailData(movieData);
            } else {
                showDetailErrorMessage();
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Movie> loader) {

        }
    };

    private LoaderManager.LoaderCallbacks<Trailer[]> trailerLoaderCallbacks
            = new LoaderManager.LoaderCallbacks<Trailer[]>() {


        @NonNull
        @Override
        public Loader<Trailer[]> onCreateLoader(int id, @Nullable final Bundle args) {
            return new AsyncTaskLoader<Trailer[]>(MovieDetailsActivity.this) {
                @Override
                protected void onStartLoading() {
                    if (args == null || !args.containsKey(MOVIE_ID)) {
                        return;
                    }
                    forceLoad();
                }

                @Nullable
                @Override
                public Trailer[] loadInBackground() {
                    int idValue = args.getInt(MOVIE_ID);
                    URL trailerRequestURL = NetworkUtils.buildMovieDateUrl(API_KEY_VALUE, String.valueOf(idValue), TRAILER);
                    try {
                        String jsonTrailerData = NetworkUtils.getResponseFromHttpUrl(trailerRequestURL);
                        return JsonUtils.parseTrailerMovieDataFromJson(jsonTrailerData);
                    } catch (Exception e) {
                        Log.e(TAG, "loadInBackground: ", e);
                        return null;
                    }
                }
            };
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Trailer[]> loader, Trailer[] data) {
            if (data == null || data.length == 0) {
                mTrailerErrorMessage.setVisibility(View.VISIBLE);
            } else {
                mTrailerErrorMessage.setVisibility(View.INVISIBLE);
                mTrailerAdapter.setTrailerData(data);
                firstVideoKey = data[0].getTrailerKey();
            }

        }

        @Override
        public void onLoaderReset(@NonNull Loader<Trailer[]> loader) {

        }
    };

    private LoaderManager.LoaderCallbacks<Review[]> reviewLoaderCallbacks
            = new LoaderManager.LoaderCallbacks<Review[]>() {


        @NonNull
        @Override
        public Loader<Review[]> onCreateLoader(int id, @Nullable final Bundle args) {
            return new AsyncTaskLoader<Review[]>(MovieDetailsActivity.this) {
                @Override
                protected void onStartLoading() {
                    if (args == null || !args.containsKey(MOVIE_ID)) {
                        return;
                    }
                    forceLoad();
                }

                @Nullable
                @Override
                public Review[] loadInBackground() {
                    int idValue = args.getInt(MOVIE_ID);
                    URL reviewRequestURL = NetworkUtils.buildMovieDateUrl(API_KEY_VALUE, String.valueOf(idValue), REVIEWS);
                    try {
                        String jsonReviewData = NetworkUtils.getResponseFromHttpUrl(reviewRequestURL);
                        return JsonUtils.parseReviewMovieDataFromJson(jsonReviewData);
                    } catch (Exception e) {
                        Log.e(TAG, "loadInBackground: ", e);
                        return null;
                    }
                }
            };
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Review[]> loader, Review[] data) {
            if (data == null || data.length == 0) {
                mReviewErrorMessage.setVisibility(View.VISIBLE);
            } else {
                mReviewErrorMessage.setVisibility(View.INVISIBLE);
                mReviewsAdapter.setReviewData(data);
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Review[]> loader) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        Log.e(TAG, "onCreate: 1 " + isDatabaseMember);
        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_DATA)) {
            mMovie = savedInstanceState.getParcelable(MOVIE_DATA);
        }

        if (isDatabaseMember) {
            favoriteButtonUi();
        } else {
            unFavoriteButtonUi();
        }

        loaderManager = getSupportLoaderManager();

        mDb = AppDatabase.getInstance(getApplicationContext());

        Intent intent = getIntent();
        if (intent == null) {
            closeActivity();
        }

        if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            Log.e(TAG, "onCreate: 2 " + isDatabaseMember);
            movieId = intent.getIntExtra(Intent.EXTRA_TEXT, -1);
            if (movieId == -1) {
                closeActivity();
            }
            LoadMovieViewModelFactory factory = new LoadMovieViewModelFactory(mDb, movieId);
            final LoadMovieViewModel viewModel =
                    ViewModelProviders.of(this, factory).get(LoadMovieViewModel.class);
            viewModel.getMovie().observe(this, new Observer<Movie>() {
                @Override
                public void onChanged(@Nullable Movie movie) {
                    if (movie != null) {
                        mMovie = movie;
                        isDatabaseMember = true;
                        favoriteButtonUi();
                        showMovieDetailData(movie);
                    } else {
                        isDatabaseMember = false;
                        unFavoriteButtonUi();
                        Bundle movieBundle = new Bundle();
                        movieBundle.putInt(MOVIE_ID, movieId);
                        Loader<Movie> movieLoader = loaderManager.getLoader(MOVIE_LOADER_ID);
                        if (movieLoader == null) {
                            loaderManager.initLoader(MOVIE_LOADER_ID, movieBundle, movieLoaderCallbacks);
                        } else {
                            loaderManager.restartLoader(MOVIE_LOADER_ID, movieBundle, movieLoaderCallbacks);
                        }
                    }
                    Log.e(TAG, "onCreate: 3 " + isDatabaseMember);
                }
            });

        } else {
            closeActivity();
        }

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onCreate: 4 " + isDatabaseMember);
                if (isDatabaseMember) {
                    isDatabaseMember = false;
                    unFavoriteButtonUi();
                    AppExecutors.getsInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.movieDeo().deleteMovie(mMovie);
                        }
                    });
                } else {
                    isDatabaseMember = true;
                    favoriteButtonUi();
                    AppExecutors.getsInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.movieDeo().insertMovie(mMovie);
                        }
                    });

                }
                Log.e(TAG, "onCreate: 5 " + isDatabaseMember);
            }

        });

        Bundle trailerBundle = new Bundle();
        trailerBundle.putInt(MOVIE_ID, movieId);
        Loader<Trailer> trailerLoader = loaderManager.getLoader(TRAILER_LOADER_ID);
        if (trailerLoader == null) {
            loaderManager.initLoader(TRAILER_LOADER_ID, trailerBundle, trailerLoaderCallbacks);
        } else {
            loaderManager.restartLoader(TRAILER_LOADER_ID, trailerBundle, trailerLoaderCallbacks);
        }
        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this);
        mTrailersRecyclerView.setHasFixedSize(true);
        mTrailersRecyclerView.setLayoutManager(trailerLayoutManager);
        mTrailerAdapter = new TrailersAdapter(this);
        mTrailersRecyclerView.setAdapter(mTrailerAdapter);

        Bundle reviewBundle = new Bundle();
        reviewBundle.putInt(MOVIE_ID, movieId);
        Loader<Review> reviewLoader = loaderManager.getLoader(REVIEW_LOADER_ID);
        if (reviewLoader == null) {
            loaderManager.initLoader(REVIEW_LOADER_ID, reviewBundle, reviewLoaderCallbacks);
        } else {
            loaderManager.restartLoader(REVIEW_LOADER_ID, reviewBundle, reviewLoaderCallbacks);
        }
        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this);
        mReviewsRecyclerView.setHasFixedSize(true);
        mReviewsRecyclerView.setLayoutManager(reviewLayoutManager);
        mReviewsAdapter = new ReviewsAdapter();
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIE_DATA, mMovie);
    }

    private void unFavoriteButtonUi() {
        mFavoriteButton.setImageResource(R.drawable.ic_favorite_border);
    }

    private void favoriteButtonUi() {
        mFavoriteButton.setImageResource(R.drawable.ic_favorite_red);
    }


    private void closeActivity() {
        finish();
        Toast.makeText(this, getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
    }

    private void showMovieDetailData(Movie movieData) {
        mMovieProgressBar.setVisibility(View.GONE);
        mMovieDataLinearLayout.setVisibility(View.VISIBLE);
        Picasso.get().load(IMAGE_URL + movieData.getPosterPath())
                .placeholder(R.mipmap.loading)
                .error(R.mipmap.error)
                .into(mPosterMovieImageView);
        mOriginalTitleTextView.setText(movieData.getOriginalTitle());
        mOverviewTextView.setText(movieData.getOverview());
        mVoteAverageTextView.setText(movieData.getVoteAverage() + " " + getString(R.string.max_rate_value));
        mReleaseDateTextView.setText(movieData.getReleaseDate().substring(0, 4));
        mRunTimeTextView.setText(String.valueOf(movieData.getRunTime()) + " " + getString(R.string.unit_time));

    }

    private void showDetailErrorMessage() {
        mPosterMovieImageView.setVisibility(View.GONE);
        mMovieProgressBar.setVisibility(View.GONE);
        mMovieDataLinearLayout.setVisibility(View.GONE);
        mDetailErrorMessageTextView.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(String trailerKey) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_APP_URL + trailerKey));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_URL + trailerKey));
        try {
            this.startActivity(appIntent);
        } catch (ActivityNotFoundException e) {
            if (webIntent.resolveActivity(getPackageManager()) != null) {
                this.startActivity(webIntent);
            } else {
                Log.e(TAG, "onClick: ", e);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.share_action) {
            String mimeType = "text/plain";
            String title = "Share Trailer Movie Video";
            String videoUrl = "http://www.youtube.com/watch?v=" + firstVideoKey;

            ShareCompat.IntentBuilder.from(this)
                    .setChooserTitle(title)
                    .setType(mimeType)
                    .setText(videoUrl)
                    .startChooser();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
