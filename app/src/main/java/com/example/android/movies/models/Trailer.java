package com.example.android.movies.models;

import com.google.gson.annotations.SerializedName;

public class Trailer {
    @SerializedName("name")
    private String mTrailerName;
    @SerializedName("key")
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
