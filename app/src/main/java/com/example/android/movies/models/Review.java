package com.example.android.movies.models;

public class Review {

    private String mReviewAuthor;
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
