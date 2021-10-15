package kh.ad.movieapp.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import kh.ad.movieapp.models.MovieModel;

// This class is for requesting single movie
public class MovieResponse {
    // 1- Finding the Movie Object
    @SerializedName("results")
    @Expose()
    private MovieModel movie;

    public MovieModel getMovie(){
        return movie;
    }

    @Override
    public String toString() {
        return "MovieResponse{" +
                "movie=" + movie +
                '}';
    }
}
