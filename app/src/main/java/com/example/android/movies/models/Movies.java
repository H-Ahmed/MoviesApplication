package com.example.android.movies.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Movies implements Parcelable {

    private String mPosterPath;
    private String mId;

    public String getPosterPath() {
        return mPosterPath;
    }

    public String getId() {
        return mId;
    }


    public Movies(String posterPath, String id) {
        mId = id;
        mPosterPath = posterPath;

    }


    protected Movies(Parcel in) {
        mPosterPath = in.readString();
        mId = in.readString();
    }

    public static final Creator<Movies> CREATOR = new Creator<Movies>() {
        @Override
        public Movies createFromParcel(Parcel in) {
            return new Movies(in);
        }

        @Override
        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mPosterPath);
        parcel.writeString(mId);
    }
}
