package com.example.android.movies.utilities;


import com.example.android.movies.models.Movie;
import com.example.android.movies.models.Movies;
import com.example.android.movies.models.Review;
import com.example.android.movies.models.Trailer;
import com.google.gson.Gson;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class JsonUtils {
    private static final String RESULTS = "results";
    private static final String MOVIE_ID = "id";
    private static final String POSTER_PATH = "poster_path";
    private static final String TRAILER_KEY = "key";
    private static final String TRAILER_NAME = "name";
    private static final String REVIEW_AUTHOR = "author";
    private static final String REVIEW_CONTENT = "content";

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
        Trailer[] parseTrailerData = new Trailer[trailerArray.length()];
        for (int i = 0; i < trailerArray.length(); i++) {
            JSONObject trailerData = trailerArray.getJSONObject(i);
            String trailerKey = trailerData.getString(TRAILER_KEY);
            String trailerName = trailerData.getString(TRAILER_NAME);
            parseTrailerData[i] = new Trailer(trailerName, trailerKey);
        }
        return parseTrailerData;
    }

    public static Review[] parseReviewMovieDataFromJson(String jsonData) throws JSONException {
        JSONObject reviewsData = new JSONObject(jsonData);
        JSONArray reviewArray = reviewsData.getJSONArray(RESULTS);
        Review[] parseReviewData = new Review[reviewArray.length()];
        for (int i = 0; i < reviewArray.length(); i++) {
            JSONObject reviewData = reviewArray.getJSONObject(i);
            String reviewAuthor = reviewData.getString(REVIEW_AUTHOR);
            String reviewContent = reviewData.getString(REVIEW_CONTENT);
            parseReviewData[i] = new Review(reviewAuthor, reviewContent);
        }
        return parseReviewData;
    }

}
