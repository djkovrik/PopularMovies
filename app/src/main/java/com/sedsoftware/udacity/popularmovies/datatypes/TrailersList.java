package com.sedsoftware.udacity.popularmovies.datatypes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("ALL")
public class TrailersList {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("results")
    @Expose
    public List<Trailer> results = null;


    public List<Trailer> getTrailersList() {
        return results;
    }
}