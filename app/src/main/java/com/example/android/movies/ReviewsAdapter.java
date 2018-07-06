package com.example.android.movies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.movies.models.Review;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    private Context mContext;
    private Review[] mReviewData;

    public ReviewsAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.review_recycle_view_item, parent, false);
        return new ReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapterViewHolder holder, int position) {
        String reviewAuthor = mReviewData[position].getReviewAuthor();
        holder.mReviewAuthorTextView.setText(reviewAuthor);
        String reviewContent = mReviewData[position].getReviewCommit();
        holder.mReviewContentTextView.setText(reviewContent);
    }

    @Override
    public int getItemCount() {
        if (mReviewData == null) {
            return 0;
        } else {
            return mReviewData.length;
        }
    }

    public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView mReviewAuthorTextView;
        private TextView mReviewContentTextView;

        public ReviewsAdapterViewHolder(View itemView) {
            super(itemView);
            mReviewAuthorTextView = itemView.findViewById(R.id.review_author);
            mReviewContentTextView = itemView.findViewById(R.id.review_content);
        }
    }

    public void setReviewData(Review[] dataReview) {
        mReviewData = dataReview;
        notifyDataSetChanged();
    }


}
