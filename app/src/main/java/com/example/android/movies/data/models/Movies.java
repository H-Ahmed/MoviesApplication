package com.example.android.movies.data.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Movies implements Parcelable {

    private String mPosterPath;
    private int mId;

    public String getPosterPath() {
        return mPosterPath;
    }

    public int getId() {
        return mId;
    }

    public Movies(String posterPath, int id) {
        mId = id;
        mPosterPath = posterPath;
    }


    protected Movies(Parcel in) {
        mPosterPath = in.readString();
        mId = in.readInt();
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
        parcel.writeInt(mId);
    }
}
