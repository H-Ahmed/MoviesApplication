package com.example.android.movies.models;

import com.google.gson.annotations.SerializedName;

public class Review {
    @SerializedName("author")
    private String mReviewAuthor;
    @SerializedName("content")
    private String mReviewCommit;

    public String getReviewAuthor() {
        return mReviewAuthor;
    }

    public String getReviewCommit() {
        return mReviewCommit;
    }

    public Review(String reviewAuthor, String reviewCommit) {
        mReviewAuthor = reviewAuthor;
        mReviewCommit = reviewCommit;
    }
}
