package com.sedsoftware.udacity.popularmovies.utils;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.sedsoftware.udacity.popularmovies.database.MoviesContract;
import com.sedsoftware.udacity.popularmovies.datatypes.Movie;
import com.sedsoftware.udacity.popularmovies.sync.MoviesAuthenticatorService;

import java.util.List;

public class SyncUtils {

    // Sync interval settings, run once a day
    private static final long SECONDS_PER_MINUTE = 60L;
    private static final long SYNC_INTERVAL_IN_MINUTES = 1400L;
    private static final long SYNC_INTERVAL =
            SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;

    private static final String CONTENT_AUTHORITY = MoviesContract.CONTENT_AUTHORITY;
    private static final String PREF_SETUP_COMPLETED = "setup_completed";


    /**
     * Creates a new account and adds it to the system account list.
     *
     * @param context App context.
     */
    public static void createSyncAccount(Context context) {

        boolean newAccount = false;
        boolean setupCompleted = PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean(PREF_SETUP_COMPLETED, false);

        Account account = MoviesAuthenticatorService.getAccount();
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        if (accountManager.addAccountExplicitly(account, null, null)) {
            ContentResolver.setIsSyncable(account, CONTENT_AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(account, CONTENT_AUTHORITY, true);
            ContentResolver.addPeriodicSync(
                    account, CONTENT_AUTHORITY, new Bundle(), SYNC_INTERVAL);
            newAccount = true;
        }

        if (newAccount || !setupCompleted) {
            forcedRefresh();
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putBoolean(PREF_SETUP_COMPLETED, true).apply();
        }
    }


    /**
     * Forced data sync, ignores all sync preferences.
     */
    private static void forcedRefresh() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        ContentResolver.requestSync(
                MoviesAuthenticatorService.getAccount(),
                MoviesContract.CONTENT_AUTHORITY,
                bundle);
    }


    /**
     * Converts List of Movie objects to ContentValues array.
     *
     * @param list Movie objects list.
     * @return ContentValues array, ready for db insertion
     */
    public static ContentValues[] makeContentFromMoviesList(List<Movie> list) {

        if (list == null) {
            return null;
        }

        ContentValues[] result = new ContentValues[list.size()];

        for (int i = 0; i < list.size(); i++) {
            Movie movie = list.get(i);
            ContentValues movieValues = new ContentValues();
            movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, movie.getMovieId());
            movieValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
            movieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
            movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
            movieValues.put(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
            movieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());

            result[i] = movieValues;
        }

        return result;
    }
}
