package com.sedsoftware.udacity.popularmovies.networking;

import com.sedsoftware.udacity.popularmovies.datatypes.MoviesList;
import com.sedsoftware.udacity.popularmovies.datatypes.ReviewsList;
import com.sedsoftware.udacity.popularmovies.datatypes.TrailersList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface which defines possible HTTP call methods.
 */

@SuppressWarnings("SameParameterValue")
public interface MoviedbApi {

    // Target URL
    String API_ENDPOINT = "http://api.themoviedb.org/";

    /**
     * Sends GET request to /3/movie/popular or /3/movie/top_rated
     *
     * @param sortingKey Sorting criteria (popular or top_rated)
     * @param apiKey     MovieDB API key
     * @return MoviesList which contains a list of Movie objects.
     */
    @GET("/3/movie/{sorting}")
    Call<MoviesList> getMovies(@Path("sorting") String sortingKey, @Query("api_key") String apiKey);

    /**
     * Sends GET request to /3/movie/{id}/videos
     *
     * @param movieId id from the Movie class
     * @param apiKey  MovieDB API key
     * @return TrailersList which contains a list of Trailer objects.
     */
    @GET("/3/movie/{id}/videos")
    Call<TrailersList> getTrailers(@Path("id") int movieId, @Query("api_key") String apiKey);

    /**
     * Sends GET request to /3/movie/{id}/reviews
     *
     * @param movieId id from the Movie class
     * @param apiKey  MovieDB API key
     * @return ReviewsList which contains a list of Review objects.
     */
    @GET("/3/movie/{id}/reviews")
    Call<ReviewsList> getReviews(@Path("id") int movieId, @Query("api_key") String apiKey);
}
