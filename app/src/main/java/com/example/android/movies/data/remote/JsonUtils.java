package com.example.android.movies.data.remote;


import com.example.android.movies.data.models.Movie;
import com.example.android.movies.data.models.Movies;
import com.example.android.movies.data.models.Review;
import com.example.android.movies.data.models.Trailer;
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
            int movieId = movieData.getInt(MOVIE_ID);
            String moviePosterPath = movieData.getString(POSTER_PATH);
            parseMoviesData[i] = new Movies(moviePosterPath, movieId);
        }
        return parseMoviesData;

    }

    public static Movie parseMovieDataFromJson(String jsonData) {
        Gson gson = new Gson();
        return gson.fromJson(jsonData, Movie.class);
    }

    public static Trailer[] parseTrailerMovieDataFromJson(String jsonData) throws JSONException {
        JSONObject trailersData = new JSONObject(jsonData);
        JSONArray trailerArray = trailersData.getJSONArray(RESULTS);
        Gson gson = new Gson();
        return gson.fromJson(trailerArray.toString(), Trailer[].class);

    }

    public static Review[] parseReviewMovieDataFromJson(String jsonData) throws JSONException {
        JSONObject reviewsData = new JSONObject(jsonData);
        JSONArray reviewArray = reviewsData.getJSONArray(RESULTS);
        Gson gson = new Gson();
        return gson.fromJson(reviewArray.toString(), Review[].class);
    }

}
