package com.sedsoftware.udacity.popularmovies.adapters;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sedsoftware.udacity.popularmovies.R;
import com.sedsoftware.udacity.popularmovies.database.MoviesContract.MovieEntry;
import com.sedsoftware.udacity.popularmovies.transformations.RoundedCornersTransformation;
import com.sedsoftware.udacity.popularmovies.utils.UriBuilders;
import com.squareup.picasso.Picasso;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private final GridItemClickListener mClickListener;
    private final Context mContext;
    private Cursor mCursor;

    public MoviesAdapter(GridItemClickListener listener, Context context) {
        this.mClickListener = listener;
        this.mContext = context;
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_movie_grid, parent, false);

        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {
        int movieIdIndex = mCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID);
        int posterIndex = mCursor.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH);

        mCursor.moveToPosition(position);

        int movieId = mCursor.getInt(movieIdIndex);
        String posterPath = mCursor.getString(posterIndex);

        holder.itemView.setTag(movieId);
        String posterUrl = UriBuilders.buildPosterUrl(posterPath);
        holder.loadPoster(posterUrl);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Cursor swapCursor(Cursor c) {
        if (mCursor == c) {
            return null;
        }

        Cursor temp = mCursor;
        this.mCursor = c;

        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    public interface GridItemClickListener {
        void onGridItemClick(int clickedMovieId);
    }

    class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mPosterImageView;

        MoviesAdapterViewHolder(View itemView) {
            super(itemView);
            mPosterImageView = (ImageView) itemView.findViewById(R.id.iv_movie_poster);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int index = mCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID);
            int movieId = mCursor.getInt(index);
            mClickListener.onGridItemClick(movieId);
        }

        private void loadPoster(String posterUrl) {
            int imageCornerRadius = mContext.getResources().getInteger(R.integer.grid_image_corner_radius);
            int imageMargin = mContext.getResources().getInteger(R.integer.grid_image_margin);

            Picasso.with(mContext)
                    .load(posterUrl)
                    .placeholder(R.xml.progress_animation)
                    .error(R.drawable.error_placeholder_image)
                    .transform(new RoundedCornersTransformation(imageCornerRadius, imageMargin))
                    .into(mPosterImageView);
        }
    }
}
