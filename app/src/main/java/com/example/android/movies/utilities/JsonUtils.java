package com.example.android.movies.utilities;



import com.example.android.movies.models.Movie;
import com.example.android.movies.models.Movies;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class JsonUtils {
    private static final String RESULTS = "results";
    private static final String MOVIE_ID = "id";
    private static final String POSTER_PATH = "poster_path";

    public static Movies[] parseMoviesDataFromJson(String jsonData) throws JSONException {

        JSONObject moviesData = new JSONObject(jsonData);
        JSONArray moviesArray = moviesData.getJSONArray(RESULTS);
        Movies[] parseMoviesData = new Movies[moviesArray.length()];
        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movieData = moviesArray.getJSONObject(i);
            String movieId = movieData.getString(MOVIE_ID);
            String moviePosterPath = movieData.getString(POSTER_PATH);
            parseMoviesData[i] = new Movies(moviePosterPath, movieId);
        }
        return parseMoviesData;

    }

    public static Movie parseMovieDataFromJson(String jsonData) {
        Gson gson = new Gson();
        Movie movie = gson.fromJson(jsonData, Movie.class);
        return movie;
    }

}
