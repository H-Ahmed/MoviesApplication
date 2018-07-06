package com.example.android.movies.models;

import com.google.gson.annotations.SerializedName;

public class Movie {

    @SerializedName("original_title")
    private String mOriginalTitle;
    @SerializedName("poster_path")
    private String mPosterPath;
    @SerializedName("overview")
    private String mOverview;
    @SerializedName("vote_average")
    private String mVoteAverage;
    @SerializedName("release_date")
    private String mReleaseDate;
    @SerializedName("runtime")
    private int mRunTime;

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public String getOverView() {
        return mOverview;
    }

    public String getVoteAverage() {
        return mVoteAverage;
    }

    public String getReleaseDate() {
        String year = mReleaseDate.substring(0,4);
        return year;
    }

    public int getRunTime() {
        return mRunTime;
    }

    public Movie(String originalTitle
            , String posterPath
            , String overview
            , String voteAverage
            , String releaseDate
            , int runTime) {
        mOriginalTitle = originalTitle;
        mPosterPath = posterPath;
        mOverview = overview;
        mVoteAverage = voteAverage;
        mReleaseDate = releaseDate;
        mRunTime = runTime;
    }


}
