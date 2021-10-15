package kh.ad.movieapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import kh.ad.movieapp.models.MovieModel;
import kh.ad.movieapp.repositories.MovieRepository;

public class MovieListViewModel extends ViewModel {
    // this class is used for VIEWMODEL

    private MovieRepository movieRepository;

    // Constructor
    public MovieListViewModel(){
        movieRepository = MovieRepository.getInstance();
    }

    public LiveData<List<MovieModel>> getmMovies() {
        return movieRepository.getMovies();
    }

    public LiveData<List<MovieModel>> getPopmMovies() {
        return movieRepository.getPopularMovies();
    }

    // 3- calling method in viewmodel
    public void searchMovieApi(String query, int pageNumber){
        movieRepository.searchMovieApi(query, pageNumber);
    }

    // 3- calling method in viewmodel
    public void searchPopularMoviesApi(int pageNumber){
        movieRepository.searchPopularMoviesApi(pageNumber);
    }

    public void searchNextPage(){
        movieRepository.searchNextPage();
    }

    public void searchNextPagePopular(){
        movieRepository.searchNextPagePopular();
    }
}
