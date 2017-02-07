package com.sedsoftware.udacity.popularmovies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sedsoftware.udacity.popularmovies.database.MoviesContract.MovieEntry;

class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableSqlQuery(MovieEntry.TABLE_NAME_POPULAR));
        db.execSQL(createTableSqlQuery(MovieEntry.TABLE_NAME_TOP_RATED));
        db.execSQL(createTableSqlQuery(MovieEntry.TABLE_NAME_FAVORITES));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME_POPULAR);
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME_TOP_RATED);
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME_FAVORITES);
        onCreate(db);
    }

    private static String createTableSqlQuery(String tableName) {
        return "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";
    }
}
