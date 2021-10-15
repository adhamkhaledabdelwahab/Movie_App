package kh.ad.movieapp.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import kh.ad.movieapp.models.MovieModel;
import kh.ad.movieapp.request.MovieApiClient;

public class MovieRepository {
    // This class is acting as repository

    private static MovieRepository instance;

    private MovieApiClient movieApiClient;
    private MovieApiClient popularMovieApiClient;

    private String mQuery;
    private int mPageNumber;

    public static MovieRepository getInstance(){
        if (instance == null)
            instance = new MovieRepository();
        return instance;
    }

    private MovieRepository(){
        movieApiClient = MovieApiClient.getInstance();
    }

    public LiveData<List<MovieModel>> getMovies(){
        return movieApiClient.getMovies();
    }

    public LiveData<List<MovieModel>> getPopularMovies(){
        return movieApiClient.getMoviesPop();
    }

    // 2- calling the method in repository
    public void searchMovieApi(String query, int pageNumber){
        mQuery = query;
        mPageNumber = pageNumber;
        movieApiClient.searchMoviesApi(query, pageNumber);
    }

    // 2- calling the method in repository
    public void searchPopularMoviesApi(int pageNumber){
        mPageNumber = pageNumber;
        movieApiClient.searchPopularMoviesApi(pageNumber);
    }

    public void searchNextPage(){
        searchMovieApi(mQuery, mPageNumber+1);
    }

    public void searchNextPagePopular(){
        searchPopularMoviesApi(mPageNumber+1);
    }
}
