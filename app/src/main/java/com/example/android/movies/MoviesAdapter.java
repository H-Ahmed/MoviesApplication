package com.example.android.movies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.movies.models.Movies;
import com.squareup.picasso.Picasso;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private Context mContext;
    private Movies[] mMoviesData;

    private final MoviesAdapterOnClickHandler mClickHandler;

    public interface MoviesAdapterOnClickHandler {
        void onClick(String movieId);
    }

    public MoviesAdapter(Context context, MoviesAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item, viewGroup, false);
        return new MoviesAdapterViewHolder(view);
    }

    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {
        String posterPath = mMoviesData[position].getPosterPath();
        Picasso.get().load("http://image.tmdb.org/t/p/w185//" + posterPath).into(holder.moviePosterImageView);
    }

    @Override
    public int getItemCount() {
        if (mMoviesData == null) {
            return 0;
        } else {
            return mMoviesData.length;
        }
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView moviePosterImageView;

        public MoviesAdapterViewHolder(View view) {
            super(view);
            moviePosterImageView = view.findViewById(R.id.iv_movie_poster);

            int gridHeight = mContext.getResources().getDisplayMetrics().heightPixels;
            moviePosterImageView.setMinimumHeight(gridHeight / 2);

            view.setOnClickListener(this);
        }

        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movies movie = mMoviesData[adapterPosition];
            mClickHandler.onClick(movie.getId());
        }

    }

    public void setMoviesData(Movies[] moviesData) {
        mMoviesData = moviesData;
        notifyDataSetChanged();
    }
}
