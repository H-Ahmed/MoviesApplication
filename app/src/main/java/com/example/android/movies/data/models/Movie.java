package com.example.android.movies.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


@Entity(tableName = "movie")
public class Movie implements Parcelable{

    @SerializedName("id")
    @ColumnInfo(name = "id")
    @PrimaryKey
    private int mId;

    @SerializedName("original_title")
    @ColumnInfo(name = "original_title")
    private String mOriginalTitle;

    @SerializedName("poster_path")
    @ColumnInfo(name = "poster_path")
    private String mPosterPath;

    @SerializedName("overview")
    @ColumnInfo(name = "overview")
    private String mOverview;

    @SerializedName("vote_average")
    @ColumnInfo(name = "vote_average")
    private float mVoteAverage;

    @SerializedName("release_date")
    @ColumnInfo(name = "release_date")
    private String mReleaseDate;

    @SerializedName("runtime")
    @ColumnInfo(name = "runtime")
    private int mRunTime;

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public void setOriginalTitle(String originalTitle) {
        mOriginalTitle = originalTitle;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public void setVoteAverage(float voteAverage) {
        mVoteAverage = voteAverage;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public void setRunTime(int runTime) {
        mRunTime = runTime;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public String getOverview() {
        return mOverview;
    }

    public float getVoteAverage() {
        return mVoteAverage;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public int getRunTime() {
        return mRunTime;
    }

    public Movie(int id
            , String originalTitle
            , String posterPath
            , String overview
            , float voteAverage
            , String releaseDate
            , int runTime) {
        mId = id;
        mOriginalTitle = originalTitle;
        mPosterPath = posterPath;
        mOverview = overview;
        mVoteAverage = voteAverage;
        mReleaseDate = releaseDate;
        mRunTime = runTime;
    }

    protected Movie(Parcel in) {
        mId = in.readInt();
        mOriginalTitle = in.readString();
        mPosterPath = in.readString();
        mOverview = in.readString();
        mVoteAverage = in.readFloat();
        mReleaseDate = in.readString();
        mRunTime = in.readInt();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeString(mOriginalTitle);
        parcel.writeString(mPosterPath);
        parcel.writeString(mOverview);
        parcel.writeFloat(mVoteAverage);
        parcel.writeString(mReleaseDate);
        parcel.writeInt(mRunTime);
    }
}
