package com.sedsoftware.udacity.popularmovies.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.sedsoftware.udacity.popularmovies.BuildConfig;
import com.sedsoftware.udacity.popularmovies.database.MoviesContract;
import com.sedsoftware.udacity.popularmovies.database.MoviesContract.MovieEntry;
import com.sedsoftware.udacity.popularmovies.datatypes.Movie;
import com.sedsoftware.udacity.popularmovies.datatypes.MoviesList;
import com.sedsoftware.udacity.popularmovies.networking.MoviedbConnection;
import com.sedsoftware.udacity.popularmovies.utils.NotificationUtils;
import com.sedsoftware.udacity.popularmovies.utils.SyncUtils;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

class MoviesSyncAdapter extends AbstractThreadedSyncAdapter {

    // Some constants needed for our GET requests
    private static final String API_KEY = BuildConfig.MOVIEDB_API_KEY;
    private static final String SORT_POPULAR = MoviesContract.PATH_POPULAR;
    private static final String SORT_TOP_RATED = MoviesContract.PATH_TOP_RATED;

    // Content resolver which performs DB operations
    private final ContentResolver mContentResolver;

    // Movies lists
    private List<Movie> mMoviesPopular;
    private List<Movie> mMoviesTopRated;

    MoviesSyncAdapter(Context context) {
        super(context, true);
        mContentResolver = context.getContentResolver();
    }

    private static List<Movie> loadMovies(String sortingKey) throws IOException {
        Call<MoviesList> movies = MoviedbConnection.getApi().getMovies(sortingKey, API_KEY);
        Response<MoviesList> response = movies.execute();
        return response.body().getMoviesList();
    }

    @Override
    public void onPerformSync(Account account,
                              Bundle extras,
                              String authority,
                              ContentProviderClient provider,
                              SyncResult syncResult) {

        try {
            mMoviesPopular = loadMovies(SORT_POPULAR);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mMoviesTopRated = loadMovies(SORT_TOP_RATED);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Popular table
        ContentValues[] contentPopular = SyncUtils.makeContentFromMoviesList(mMoviesPopular);

        if (contentPopular != null && contentPopular.length > 0) {
            mContentResolver.delete(MovieEntry.CONTENT_URI_POPULAR, null, null);
            mContentResolver.bulkInsert(MovieEntry.CONTENT_URI_POPULAR, contentPopular);
        }

        // Top Rated table
        ContentValues[] contentTopRated = SyncUtils.makeContentFromMoviesList(mMoviesTopRated);

        if (contentTopRated != null && contentTopRated.length > 0) {
            mContentResolver.delete(MovieEntry.CONTENT_URI_TOP_RATED, null, null);
            mContentResolver.bulkInsert(MovieEntry.CONTENT_URI_TOP_RATED, contentTopRated);
        }

        NotificationUtils.notifyUserAboutUpdate(getContext());
    }
}
