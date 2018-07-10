package com.example.android.movies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.movies.models.Trailer;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersAdapterViewHolder> {


    private Trailer[] mTrailerData;

    private final TrailersAdapterOnClickHandler mClickHandler;

    public TrailersAdapter(TrailersAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public interface TrailersAdapterOnClickHandler {
        void onClick(String trailerKey);
    }

    @NonNull
    @Override
    public TrailersAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_recycle_view_item, parent, false);
        return new TrailersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailersAdapterViewHolder holder, int position) {
        String trailerName = mTrailerData[position].getTrailerName();
        holder.mTrailerNameTextView.setText(trailerName);
    }

    @Override
    public int getItemCount() {
        if (mTrailerData == null) {
            return 0;
        } else {
            return mTrailerData.length;
        }
    }

    public class TrailersAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTrailerNameTextView;

        public TrailersAdapterViewHolder(View itemView) {
            super(itemView);
            mTrailerNameTextView = itemView.findViewById(R.id.tv_trailer_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Trailer trailer = mTrailerData[adapterPosition];
            mClickHandler.onClick(trailer.getTrailerKey());
        }
    }

    public void setTrailerData(Trailer[] dataReview) {
        mTrailerData = dataReview;
        notifyDataSetChanged();
    }
}
