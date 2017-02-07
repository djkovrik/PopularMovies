package com.sedsoftware.udacity.popularmovies.utils;

import android.net.Uri;

public class UriBuilders {

    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";
    private static final String YOUTUBE_POSTER_URL = "http://img.youtube.com/vi/%s/mqdefault.jpg";
    private static final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/";
    private static final String NORMAL_QUALITY_PFIX = "w342";
    private static final String HIGH_QUALITY_PFIX = "w500";

    /**
     * Builds full poster image URL.
     *
     * @param path Poster path record from the database.
     * @return Full image url.
     */
    public static String buildPosterUrl(String path) {
        return BASE_POSTER_URL + NORMAL_QUALITY_PFIX + path;
    }

    /**
     * Builds full backdrop image URL.
     *
     * @param path Backdrop path record from the database.
     * @return Full image url.
     */
    public static String buildBackdropUrl(String path) {
        return BASE_POSTER_URL + HIGH_QUALITY_PFIX + path;
    }

    /**
     * Builds youtube video URL.
     *
     * @param key Video key.
     * @return Full video url.
     */
    public static String buildYoutubeUrl(String key) {
        return YOUTUBE_BASE_URL + key;
    }

    /**
     * Builds youtube preview image URL.
     *
     * @param key Video key.
     * @return Full image url.
     */
    public static String buildYoutubeThumbnailUrl(String key) {
        return String.format(YOUTUBE_POSTER_URL, key);
    }

    /**
     * Builds details uri based on movie id.
     *
     * @param baseUri Base content uri.
     * @param movieId Movie id.
     * @return Details content uri.
     */
    public static Uri buildDetailsUri(Uri baseUri, int movieId) {
        return baseUri.buildUpon()
                .appendPath(String.valueOf(movieId))
                .build();
    }
}
