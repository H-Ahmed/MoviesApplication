package com.example.android.movies;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.movies.data.models.Movie;
import com.example.android.movies.data.models.Movies;

import com.example.android.movies.data.remote.JsonUtils;
import com.example.android.movies.data.local.MainViewModel;
import com.example.android.movies.data.remote.NetworkUtils;
import com.example.android.movies.ui.MoviesAdapter;

import java.net.URL;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler {

    @BindView(R.id.pb_movies)
    ProgressBar mMoviesProgressBar;
    @BindView(R.id.rv_movies)
    RecyclerView mMoviesRecyclerView;
    @BindView(R.id.tv_error_message)
    TextView mErrorMessageTextView;


    private static final String TAG = "MainActivity";

    private static final int MOVIES_LOADER_ID = 15;
    private static final String MOVIES_ORDER_BY_KEY = "order_by";
    private static final String MOVIES_DATA_KEY = "moviesData";

    private String orderBy;
    private static String API_KEY_VALUE;

    private Movies[] mMoviesData;
    private MoviesAdapter mAdapter;

    private SharedPreferences orderByPreferences;

    private LoaderManager mLoaderManager;

    private LoaderManager.LoaderCallbacks<Movies[]> moviesLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<Movies[]>() {
                @NonNull
                @Override
                public Loader<Movies[]> onCreateLoader(int id, @Nullable final Bundle args) {
                    return new AsyncTaskLoader<Movies[]>(MainActivity.this) {
                        @Override
                        protected void onStartLoading() {
                            if (args == null || !args.containsKey(MOVIES_ORDER_BY_KEY)) {
                                showErrorMessage();
                                return;
                            }
                            mMoviesProgressBar.setVisibility(View.VISIBLE);
                            if (mMoviesData != null) {
                                deliverResult(mMoviesData);
                            } else {
                                forceLoad();
                            }
                        }

                        @Nullable
                        @Override
                        public Movies[] loadInBackground() {
                            String orderValue = args.getString(MOVIES_ORDER_BY_KEY);
                            if (orderValue == null || TextUtils.isEmpty(orderValue)) {
                                return null;
                            }
                            URL moviesRequestURL = NetworkUtils.buildUrl(API_KEY_VALUE, orderValue);
                            Log.e(TAG, "loadInBackground: " + moviesRequestURL.toString());
                            try {
                                String jsonMoviesData = NetworkUtils.getResponseFromHttpUrl(moviesRequestURL);
                                Log.e(TAG, "loadInBackground: " + jsonMoviesData);
                                return JsonUtils.parseMoviesDataFromJson(jsonMoviesData);
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage(), e);
                                return null;
                            }
                        }

                        @Override
                        public void deliverResult(@Nullable Movies[] data) {
                            mMoviesData = data;
                            super.deliverResult(data);
                        }
                    };
                }

                @Override
                public void onLoadFinished(@NonNull Loader<Movies[]> loader, Movies[] moviesData) {
                    mMoviesProgressBar.setVisibility(View.INVISIBLE);
                    if (moviesData != null) {
                        mMoviesData = moviesData;
                        showMoviesData();
                        mAdapter.setMoviesData(moviesData);
                    } else {
                        mMoviesData = null;
                        showErrorMessage();
                    }
                }

                @Override
                public void onLoaderReset(@NonNull Loader<Movies[]> loader) {

                }
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIES_DATA_KEY)) {
            mMoviesData = (Movies[]) savedInstanceState.getParcelableArray(MOVIES_DATA_KEY);
        }

        mLoaderManager = getSupportLoaderManager();
        orderByPreferences = getSharedPreferences(MOVIES_ORDER_BY_KEY, MODE_PRIVATE);
        orderBy = orderByPreferences.getString(MOVIES_ORDER_BY_KEY, null);
        if (orderBy == null) {
            editOrderByPreferences(getResources().getString(R.string.sort_by_popular));
            orderBy = orderByPreferences.getString(MOVIES_ORDER_BY_KEY, getResources().getString(R.string.sort_by_popular));
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        API_KEY_VALUE = getResources().getString(R.string.api_key);

        GridLayoutManager layoutManager;
        int noOfColumns = calculateNoOfColumns(this);
        layoutManager = new GridLayoutManager(this, noOfColumns);

        mMoviesRecyclerView.setHasFixedSize(true);
        mMoviesRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MoviesAdapter(this, this);
        mMoviesRecyclerView.setAdapter(mAdapter);


        if (mMoviesData != null) {
            showMoviesData();
            mAdapter.setMoviesData(mMoviesData);
        } else {
            showErrorMessage();
            loadMovieData();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(MOVIES_DATA_KEY, mMoviesData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
    }

    @Override
    public void onClick(int movieId) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, movieId);
        startActivity(intent);
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 200;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        if (noOfColumns < 2) {
            noOfColumns = 2;
        }
        return noOfColumns;
    }

    private void editOrderByPreferences(String prefValue) {
        SharedPreferences.Editor editor = orderByPreferences.edit();
        editor.putString(MOVIES_ORDER_BY_KEY, prefValue);
        editor.commit();
    }

    private void loadMovieData() {
        showMoviesData();
        if (!orderBy.equals(getResources().getString(R.string.my_favorite))) {
            Bundle moviesBundle = new Bundle();
            moviesBundle.putString(MOVIES_ORDER_BY_KEY, orderBy);

            Loader<Movies[]> moviesLoader = mLoaderManager.getLoader(MOVIES_LOADER_ID);
            if (moviesLoader == null) {
                mLoaderManager.initLoader(MOVIES_LOADER_ID, moviesBundle, moviesLoaderCallbacks);
            } else {
                mLoaderManager.restartLoader(MOVIES_LOADER_ID, moviesBundle, moviesLoaderCallbacks);
            }
        } else {
            loadMovieDataFromDatabase();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemId = item.getItemId();
        if (menuItemId == R.id.sort_by_popular) {
            editOrderByPreferences(getResources().getString(R.string.sort_by_popular));
            orderBy = orderByPreferences.getString(MOVIES_ORDER_BY_KEY, getResources().getString(R.string.sort_by_popular));
            mMoviesData = null;
            loadMovieData();
            return true;
        }
        if (menuItemId == R.id.sort_by_top_rated) {
            editOrderByPreferences(getResources().getString(R.string.sort_by_top_rated));
            orderBy = orderByPreferences.getString(MOVIES_ORDER_BY_KEY, getResources().getString(R.string.sort_by_top_rated));
            mMoviesData = null;
            loadMovieData();
            return true;
        }
        if (menuItemId == R.id.favorite_movies) {
            editOrderByPreferences(getResources().getString(R.string.my_favorite));
            orderBy = orderByPreferences.getString(MOVIES_ORDER_BY_KEY, getResources().getString(R.string.my_favorite));
            mLoaderManager.destroyLoader(MOVIES_LOADER_ID);
            mMoviesProgressBar.setVisibility(View.GONE);
            mMoviesData = null;
            loadMovieDataFromDatabase();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMovieDataFromDatabase() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                if (orderBy.equals(getResources().getString(R.string.my_favorite))) {
                    mMoviesData = new Movies[movies.size()];
                    for (int i = 0; i < mMoviesData.length; i++) {
                        mMoviesData[i] = new Movies(movies.get(i).getPosterPath(),
                                movies.get(i).getId());
                    }
                    if (mMoviesData == null || mMoviesData.length == 0) {
                        showErrorMessage();
                    } else {
                        showMoviesData();
                        mAdapter.setMoviesData(mMoviesData);
                    }
                }
            }
        });

    }

    private void showMoviesData() {
        mMoviesRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        mMoviesRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }
}
