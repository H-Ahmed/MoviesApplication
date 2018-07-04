package com.example.android.movies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movies.models.Movies;
import com.example.android.movies.utilities.JsonUtils;
import com.example.android.movies.utilities.NetworkUtils;

import java.net.URL;


import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler {

    @BindView(R.id.pb_movies)
    ProgressBar mMoviesProgressBar;
    @BindView(R.id.rv_movies)
    RecyclerView mMoviesRecyclerView;
    @BindView(R.id.tv_error_message)
    TextView mErrorMessageTextView;

    private String orderBy;
    private static String API_KEY_VALUE;

    private Movies[] mMoviesData;
    private MoviesAdapter mAdapter;

    private static final String MOVIES_DATA_KEY = "moviesData";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIES_DATA_KEY)) {
            mMoviesData = (Movies[]) savedInstanceState.getParcelableArray(MOVIES_DATA_KEY);
        }

        orderBy = getResources().getString(R.string.sort_by_popular);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        API_KEY_VALUE = getResources().getString(R.string.api_key);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
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
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(MOVIES_DATA_KEY, mMoviesData);
    }

    @Override
    public void onClick(String movieId) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, movieId);
        startActivity(intent);
    }

    private void loadMovieData() {
        showMoviesData();
        new FetchMoviesData().execute(orderBy);

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
            orderBy = getResources().getString(R.string.sort_by_popular);
            loadMovieData();
            return true;
        }
        if (menuItemId == R.id.sort_by_top_rated) {
            orderBy = getResources().getString(R.string.sort_by_top_rated);
            loadMovieData();
            return true;
        }
        if (menuItemId == R.id.favorite_movies) {
            Toast.makeText(MainActivity.this, "My favorite movies", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMoviesData() {
        mMoviesRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        mMoviesRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    public class FetchMoviesData extends AsyncTask<String, Void, Movies[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mMoviesProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movies[] doInBackground(String... strings) {
            String orderValue = strings[0];
            URL moviesRequestURL = NetworkUtils.buildUrl(API_KEY_VALUE, orderValue);
            try {
                String jsonMoviesData = NetworkUtils.getResponseFromHttpUrl(moviesRequestURL);
                return JsonUtils.parseMoviesDataFromJson(jsonMoviesData);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final Movies[] moviesData) {
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
    }

}
