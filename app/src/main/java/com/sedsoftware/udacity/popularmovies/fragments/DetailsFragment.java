package com.sedsoftware.udacity.popularmovies.fragments;


import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sedsoftware.udacity.popularmovies.BuildConfig;
import com.sedsoftware.udacity.popularmovies.R;
import com.sedsoftware.udacity.popularmovies.database.MoviesContract.MovieEntry;
import com.sedsoftware.udacity.popularmovies.databinding.FragmentDetailsBinding;
import com.sedsoftware.udacity.popularmovies.datatypes.Review;
import com.sedsoftware.udacity.popularmovies.datatypes.ReviewsList;
import com.sedsoftware.udacity.popularmovies.datatypes.Trailer;
import com.sedsoftware.udacity.popularmovies.datatypes.TrailersList;
import com.sedsoftware.udacity.popularmovies.networking.MoviedbConnection;
import com.sedsoftware.udacity.popularmovies.utils.TextFormatUtils;
import com.sedsoftware.udacity.popularmovies.utils.UriBuilders;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_URI = "CONTENT_URI";
    private static final String ARG_ID = "MOVIE_ID";

    private static final String API_KEY = BuildConfig.MOVIEDB_API_KEY;

    private static final int ID_DETAILS_LOADER = 111;
    private static final int ID_CHECK_FAVORITES_LOADER = 222;

    private static final String[] PROJECTION = new String[]{
            MovieEntry.COLUMN_MOVIE_ID,
            MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.COLUMN_BACKDROP_PATH,
            MovieEntry.COLUMN_OVERVIEW
    };

    // --Commented out by Inspection (07.02.2017 15:09):public static final int INDEX_MOVIE_ID = 0;
    private static final int INDEX_ORIGINAL_TITLE = 1;
    private static final int INDEX_RELEASE_DATE = 2;
    private static final int INDEX_VOTE_AVERAGE = 3;
    private static final int INDEX_POSTER_PATH = 4;
    private static final int INDEX_BACKDROP_PATH = 5;
    private static final int INDEX_COLUMN_OVERVIEW = 6;

    private FragmentDetailsBinding mDetailsBinding;
    private int mMovieId;
    private Uri mMovieContentUri;
    private Cursor mCurrentMovieData;
    private boolean mMarkedAsFavorite;

    private LinearLayout mTrailersLayout;
    private LinearLayout mReviewsLayout;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance(Uri uri, int id) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_URI, uri);
        args.putInt(ARG_ID, id);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri contentUri = getArguments().getParcelable(ARG_URI);
        mMovieId = getArguments().getInt(ARG_ID);
        mMovieContentUri = UriBuilders.buildDetailsUri(contentUri, mMovieId);
        mMarkedAsFavorite = false;

        getLoaderManager().initLoader(ID_DETAILS_LOADER, null, DetailsFragment.this);
        getLoaderManager().initLoader(ID_CHECK_FAVORITES_LOADER, null, DetailsFragment.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false);
        return mDetailsBinding.getRoot();
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTrailersLayout = mDetailsBinding.detailsScrollable.layoutTrailers;
        mReviewsLayout = mDetailsBinding.detailsScrollable.layoutReviews;

        mDetailsBinding.fabFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMarkedAsFavorite) {
                    Uri forDeletion =
                            UriBuilders.buildDetailsUri(MovieEntry.CONTENT_URI_FAVORITES, mMovieId);

                    getActivity().getContentResolver().delete(forDeletion, null, null);
                    Snackbar.make(view, R.string.favorites_movie_removed, Snackbar.LENGTH_SHORT).show();
                } else {
                    ContentValues values = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(mCurrentMovieData, values);
                    getActivity().getContentResolver().insert(MovieEntry.CONTENT_URI_FAVORITES, values);
                    Snackbar.make(view, R.string.favorites_movie_added, Snackbar.LENGTH_SHORT).show();
                }
                handleFabIcon();
                mMarkedAsFavorite = !mMarkedAsFavorite;
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        switch (loaderId) {
            // Load details
            case ID_DETAILS_LOADER:
                return new CursorLoader(
                        getActivity(),
                        mMovieContentUri,
                        PROJECTION,
                        null, null, null);

            // Check if movie already in favorites
            case ID_CHECK_FAVORITES_LOADER:
                return new CursorLoader(
                        getActivity(),
                        MovieEntry.CONTENT_URI_FAVORITES,
                        PROJECTION,
                        MovieEntry.COLUMN_MOVIE_ID + "=?",
                        new String[]{String.valueOf(mMovieId)},
                        null);

            default:
                throw new RuntimeException("Loader not implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            return;
        }

        int loaderId = loader.getId();

        switch (loaderId) {
            case ID_DETAILS_LOADER:
                populateDetailsBinding(data);
                mCurrentMovieData = data;
                loadTrailers(mMovieId);
                loadReviews(mMovieId);
                break;

            case ID_CHECK_FAVORITES_LOADER:
                if (data.getCount() > 0) {
                    mDetailsBinding.fabFavorites.setImageResource(R.drawable.ic_done_black_24dp);
                    mMarkedAsFavorite = true;
                } else {
                    mDetailsBinding.fabFavorites.setImageResource(R.drawable.ic_favorite_black_24dp);
                    mMarkedAsFavorite = false;
                }
                break;

            default:
                throw new RuntimeException("Loader not implemented: " + loaderId);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void loadPosterToView(String posterUrl, ImageView target) {
        Picasso.with(getActivity())
                .load(posterUrl)
                .placeholder(R.xml.progress_animation)
                .error(R.drawable.error_placeholder_image)
                .into(target);
    }

    private void populateDetailsBinding(Cursor data) {
        // Backdrop image
        ImageView backdropView = mDetailsBinding.ivBackdrop;
        String backdropPath = data.getString(INDEX_BACKDROP_PATH);
        String backdropUrl = UriBuilders.buildBackdropUrl(backdropPath);
        loadPosterToView(backdropUrl, backdropView);

        // Poster
        ImageView posterView = mDetailsBinding.ivPoster;
        String posterPath = data.getString(INDEX_POSTER_PATH);
        String posterUrl = UriBuilders.buildPosterUrl(posterPath);
        loadPosterToView(posterUrl, posterView);

        // Title
        String title = data.getString(INDEX_ORIGINAL_TITLE);
        mDetailsBinding.tvTitle.setText(title);

        // Date
        String releaseDate = data.getString(INDEX_RELEASE_DATE);
        String formattedDate = TextFormatUtils.getFormattedDate(releaseDate);
        mDetailsBinding.tvReleaseDate.setText(formattedDate);

        // Rating
        double rating = data.getDouble(INDEX_VOTE_AVERAGE);
        String suffix = getResources().getString(R.string.rating_suffix);
        String formattedRating = TextFormatUtils.getFormattedRating(rating, suffix);
        mDetailsBinding.tvRating.setText(formattedRating);

        // Overview
        String overview = data.getString(INDEX_COLUMN_OVERVIEW);
        mDetailsBinding.detailsScrollable.tvOverview.setText(overview);
    }

    private void handleFabIcon() {
        if (mMarkedAsFavorite) {
            mDetailsBinding.fabFavorites.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            mDetailsBinding.fabFavorites.setImageResource(R.drawable.ic_done_black_24dp);
        }
    }

    private void loadTrailers(int movieId) {

        MoviedbConnection.getApi().getTrailers(movieId, API_KEY).enqueue(new Callback<TrailersList>() {
            @Override
            public void onResponse(Call<TrailersList> call, Response<TrailersList> response) {
                if (response.body() != null) {
                    List<Trailer> trailersList = response.body().getTrailersList();

                    if (trailersList == null || trailersList.isEmpty()) {
                        mTrailersLayout.setVisibility(View.GONE);
                        mDetailsBinding.detailsScrollable.tvTrailers.setVisibility(View.GONE);
                        return;
                    }

                    for (Trailer trailer : trailersList) {
                        LinearLayout linearLayout = buildTrailerLayout(trailer);
                        mTrailersLayout.addView(linearLayout);
                    }
                }
            }

            @Override
            public void onFailure(Call<TrailersList> call, Throwable t) {
                Toast.makeText(getContext(),
                        R.string.error_no_api_connection,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private LinearLayout buildTrailerLayout(Trailer trailer) {

        String key = trailer.getKey();
        String thumbnailUrl = UriBuilders.buildYoutubeThumbnailUrl(key);
        final String url = UriBuilders.buildYoutubeUrl(key);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = getResources().getDimensionPixelOffset(R.dimen.details_half_margin);
        layout.setPadding(0, padding, padding, padding);

        ImageView previewImage = new ImageView(getContext());
        loadPosterToView(thumbnailUrl, previewImage);
        layout.addView(previewImage);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent watchIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                if (watchIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivity(watchIntent);
                }
            }
        });

        return layout;
    }

    private void loadReviews(int movieId) {

        MoviedbConnection.getApi().getReviews(movieId, API_KEY).enqueue(new Callback<ReviewsList>() {
            @Override
            public void onResponse(Call<ReviewsList> call, Response<ReviewsList> response) {
                if (response.body() != null) {
                    List<Review> reviewsList = response.body().getReviewsList();

                    if (reviewsList == null || reviewsList.isEmpty()) {
                        mReviewsLayout.setVisibility(View.GONE);
                        mDetailsBinding.detailsScrollable.tvReviews.setVisibility(View.GONE);
                        return;
                    }

                    for (Review review : reviewsList) {
                        LinearLayout linearLayout = buildReviewLayout(review);
                        mReviewsLayout.addView(linearLayout);
                    }
                }
            }

            @Override
            public void onFailure(Call<ReviewsList> call, Throwable t) {
                Toast.makeText(getContext(),
                        R.string.error_no_api_connection,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private LinearLayout buildReviewLayout(Review review) {

        Resources res = getResources();

        String nickname = review.getAuthor();
        String authorHtml = String.format(res.getString(R.string.details_review_author), nickname);
        String url = review.getUrl();
        String urlHtml = String.format(res.getString(R.string.details_review_link), url);
        String content = review.getContent();

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        int padding = getResources().getDimensionPixelOffset(R.dimen.details_half_margin);

        TextView reviewAuthor = new TextView(getContext());
        reviewAuthor.setText(fromHtml(authorHtml));
        reviewAuthor.setPadding(0, padding, 0, padding);
        layout.addView(reviewAuthor);

        TextView reviewText = new TextView(getContext());
        reviewText.setText(TextFormatUtils.shortenReviewText(content));
        reviewText.setPadding(0, 0, 0, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            reviewText.setTextAppearance(R.style.ReviewText);
        }

        layout.addView(reviewText);

        TextView reviewHyperlink = new TextView(getContext());
        reviewHyperlink.setText(fromHtml(urlHtml));
        reviewHyperlink.setClickable(true);
        reviewHyperlink.setPadding(0, 0, 0, padding);
        reviewHyperlink.setMovementMethod(LinkMovementMethod.getInstance());
        layout.addView(reviewHyperlink);

        View divider = new View(getContext());
        divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 4));
        divider.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorDivider));
        layout.addView(divider);

        return layout;
    }

    @SuppressWarnings("deprecation")
    private static CharSequence fromHtml(String html) {
        CharSequence styledText;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            styledText = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            styledText = Html.fromHtml(html);
        }
        return styledText;
    }
}
