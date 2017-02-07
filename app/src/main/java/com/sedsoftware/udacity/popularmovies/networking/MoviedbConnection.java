package com.sedsoftware.udacity.popularmovies.networking;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class MoviedbConnection {

    public static MoviedbApi getApi() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MoviedbApi.API_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(MoviedbApi.class);
    }
}
