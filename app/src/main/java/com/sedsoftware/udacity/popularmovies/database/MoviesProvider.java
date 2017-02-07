package com.sedsoftware.udacity.popularmovies.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.sedsoftware.udacity.popularmovies.database.MoviesContract.MovieEntry;

public class MoviesProvider extends ContentProvider {

    private static final int CODE_POPULAR = 100;
    private static final int CODE_POPULAR_WITH_ID = 101;
    private static final int CODE_TOP_RATED = 200;
    private static final int CODE_TOP_RATED_WITH_ID = 201;
    private static final int CODE_FAVORITES = 300;
    private static final int CODE_FAVORITES_WITH_ID = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mDbHelper;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_POPULAR, CODE_POPULAR);
        matcher.addURI(authority, MoviesContract.PATH_POPULAR + "/#", CODE_POPULAR_WITH_ID);
        matcher.addURI(authority, MoviesContract.PATH_TOP_RATED, CODE_TOP_RATED);
        matcher.addURI(authority, MoviesContract.PATH_TOP_RATED + "/#", CODE_TOP_RATED_WITH_ID);
        matcher.addURI(authority, MoviesContract.PATH_FAVORITES, CODE_FAVORITES);
        matcher.addURI(authority, MoviesContract.PATH_FAVORITES + "/#", CODE_FAVORITES_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor;
        String movieId;

        switch (sUriMatcher.match(uri)) {
            case CODE_POPULAR:
                cursor = mDbHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME_POPULAR,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_POPULAR_WITH_ID:
                movieId = uri.getLastPathSegment();
                cursor = mDbHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME_POPULAR,
                        projection,
                        MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{movieId},
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_TOP_RATED:
                cursor = mDbHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME_TOP_RATED,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_TOP_RATED_WITH_ID:
                movieId = uri.getLastPathSegment();
                cursor = mDbHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME_TOP_RATED,
                        projection,
                        MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{movieId},
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_FAVORITES:
                cursor = mDbHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME_FAVORITES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_FAVORITES_WITH_ID:
                movieId = uri.getLastPathSegment();
                cursor = mDbHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME_FAVORITES,
                        projection,
                        MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{movieId},
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri (query): " + uri);
        }

        //noinspection ConstantConditions
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Uri returnUri;
        long ids;

        switch (sUriMatcher.match(uri)) {
            case CODE_POPULAR:
                ids = db.insert(MovieEntry.TABLE_NAME_POPULAR, null, values);
                if (ids > 0) {
                    returnUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI_POPULAR, ids);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;

            case CODE_TOP_RATED:
                ids = db.insert(MovieEntry.TABLE_NAME_TOP_RATED, null, values);
                if (ids > 0) {
                    returnUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI_TOP_RATED, ids);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;

            case CODE_FAVORITES:
                ids = db.insert(MovieEntry.TABLE_NAME_FAVORITES, null, values);
                if (ids > 0) {
                    returnUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI_FAVORITES, ids);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri (insert): " + uri);
        }

        //noinspection ConstantConditions
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsInserted = 0;

        switch (sUriMatcher.match(uri)) {
            case CODE_POPULAR:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long ids = db.insert(MovieEntry.TABLE_NAME_POPULAR, null, value);
                        if (ids != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    //noinspection ConstantConditions
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;

            case CODE_TOP_RATED:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long ids = db.insert(MovieEntry.TABLE_NAME_TOP_RATED, null, value);
                        if (ids != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    //noinspection ConstantConditions
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int numRowsDeleted;
        String movieId;


        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {
            case CODE_POPULAR:
                numRowsDeleted = db.delete(
                        MovieEntry.TABLE_NAME_POPULAR,
                        selection,
                        selectionArgs);
                break;

            case CODE_TOP_RATED:
                numRowsDeleted = db.delete(
                        MovieEntry.TABLE_NAME_TOP_RATED,
                        selection,
                        selectionArgs);
                break;

            case CODE_FAVORITES:
                numRowsDeleted = db.delete(
                        MovieEntry.TABLE_NAME_FAVORITES,
                        selection,
                        selectionArgs);
                break;

            /* Deleting single row */
            case CODE_POPULAR_WITH_ID:
                movieId = uri.getLastPathSegment();
                numRowsDeleted = db.delete(MovieEntry.TABLE_NAME_POPULAR,
                        MovieEntry.COLUMN_MOVIE_ID + "=?",
                        new String[]{movieId});
                break;

            case CODE_TOP_RATED_WITH_ID:
                movieId = uri.getLastPathSegment();
                numRowsDeleted = db.delete(MovieEntry.TABLE_NAME_TOP_RATED,
                        MovieEntry.COLUMN_MOVIE_ID + "=?",
                        new String[]{movieId});
                break;

            case CODE_FAVORITES_WITH_ID:
                movieId = uri.getLastPathSegment();
                numRowsDeleted = db.delete(MovieEntry.TABLE_NAME_FAVORITES,
                        MovieEntry.COLUMN_MOVIE_ID + "=?",
                        new String[]{movieId});
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri (delete): " + uri);
        }

        if (numRowsDeleted != 0) {
            //noinspection ConstantConditions
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
