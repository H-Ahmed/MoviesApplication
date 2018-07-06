package com.example.android.movies.models;

public class Trailer {
    private String mTrailerName;
    private String mTrailerKey;

    public String getTrailerName() {
        return mTrailerName;
    }

    public String getTrailerKey() {
        return mTrailerKey;
    }

    public Trailer(String trailerName, String trailerSite) {
        mTrailerName = trailerName;
        mTrailerKey = trailerSite;
    }
}
