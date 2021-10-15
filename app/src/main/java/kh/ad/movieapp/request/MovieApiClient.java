package kh.ad.movieapp.request;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import kh.ad.movieapp.AppExecutors;
import kh.ad.movieapp.models.MovieModel;
import kh.ad.movieapp.response.MovieSearchResponse;
import kh.ad.movieapp.utils.Credentials;
import retrofit2.Call;
import retrofit2.Response;

public class MovieApiClient {

    // livedata
    private MutableLiveData<List<MovieModel>> mMovies;
    private static MovieApiClient instance;

    // making global Runnable
    private RetrieveMoviesRunnable retrieveMoviesRunnable;

    // livedata for popular moveis
    private MutableLiveData<List<MovieModel>> mMoviesPop;

    // making global Popular
    private RetrievePopularMoviesRunnable retrievePopularMoviesRunnable;

    public static MovieApiClient getInstance(){
        if (instance == null)
            instance = new MovieApiClient();
        return instance;
    }

    private MovieApiClient(){
        mMovies = new MutableLiveData<>();
        mMoviesPop = new MutableLiveData<>();
    }

    public LiveData<List<MovieModel>> getMovies(){
        return mMovies;
    }

    public LiveData<List<MovieModel>> getMoviesPop(){
        return mMoviesPop;
    }

    // 1- this method that we are going to call through classes
    public void searchMoviesApi(String query, int pageNumber) {

        if (retrieveMoviesRunnable != null)
            retrieveMoviesRunnable = null;

        retrieveMoviesRunnable = new RetrieveMoviesRunnable(query,pageNumber);

        final Future myHandler = AppExecutors
                .getInstance()
                .networkIO()
                .submit(retrieveMoviesRunnable);

        AppExecutors.getInstance().networkIO().schedule(() -> {
            // Cancel Retrofit call
            myHandler.cancel(true);
        }, 5000, TimeUnit.MILLISECONDS);
    }

    // 1- this method that we are going to call through classes
    public void searchPopularMoviesApi(int pageNumber) {

        if (retrievePopularMoviesRunnable != null)
            retrievePopularMoviesRunnable = null;

        retrievePopularMoviesRunnable = new RetrievePopularMoviesRunnable(pageNumber);

        final Future myHandler = AppExecutors
                .getInstance()
                .networkIO()
                .submit(retrievePopularMoviesRunnable);

        AppExecutors.getInstance().networkIO().schedule(() -> {
            // Cancel Retrofit call
            myHandler.cancel(true);
        }, 5000, TimeUnit.MILLISECONDS);
    }

    // Retrieve data from RestApi by runnable class
    // We have 2 types of Queries: the ID & search Queries
    private class RetrieveMoviesRunnable implements Runnable{

        private String query;
        private int pageNumber;
        boolean cancelRequest;

        public RetrieveMoviesRunnable(String query, int pageNumber) {
            this.query = query;
            this.pageNumber = pageNumber;
            this.cancelRequest = false;
        }

        @Override
        public void run() {
            // getting the response objects
            try {
                Response response = getMovies(query, pageNumber).execute();
                if (cancelRequest){
                    return;
                }
                if (response.code() == 200){
                    assert response.body() != null;
                    List<MovieModel> list = new ArrayList<>(
                            ((MovieSearchResponse)response.body()).getMovies());
                    if (pageNumber == 1){
                        // sending data to live data
                        // post value: used for background thread
                        // setValue: not for background thread
                        mMovies.postValue(list);
                    }else{
                        List<MovieModel> currentMovies = mMovies.getValue();
                        assert currentMovies != null;
                        currentMovies.addAll(list);
                        mMovies.postValue(currentMovies);
                    }
                }else{
                    assert response.errorBody() != null;
                    String error = response.errorBody().string();
                    Log.v("Tag", "Error " + error);
                    mMovies.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mMovies.postValue(null);
            }

            if (cancelRequest){
                return;
            }
        }

        // search method query
        private Call<MovieSearchResponse> getMovies(String query, int pageNumber){
            return Service.getMovieApi().searchMovie(Credentials.API_KEY,
                    query, pageNumber);
        }

        private void cancelRequest(){
            Log.v("Tag", "Cancelling Search Request");
            cancelRequest = true;
        }
    }

    private class RetrievePopularMoviesRunnable implements Runnable{

        private int pageNumber;
        boolean cancelRequest;

        public RetrievePopularMoviesRunnable(int pageNumber) {
            this.pageNumber = pageNumber;
            this.cancelRequest = false;
        }

        @Override
        public void run() {
            // getting the response objects
            try {
                Response response = getPopularMovies(pageNumber).execute();
                if (cancelRequest){
                    return;
                }
                if (response.code() == 200){
                    assert response.body() != null;
                    List<MovieModel> list = new ArrayList<>(
                            ((MovieSearchResponse)response.body()).getMovies());
                    if (pageNumber == 1){
                        // sending data to live data
                        // post value: used for background thread
                        // setValue: not for background thread
                        mMoviesPop.postValue(list);
                    }else{
                        List<MovieModel> currentMovies = mMoviesPop.getValue();
                        assert currentMovies != null;
                        currentMovies.addAll(list);
                        mMoviesPop.postValue(currentMovies);
                    }
                }else{
                    assert response.errorBody() != null;
                    String error = response.errorBody().string();
                    Log.v("Tag", "Error " + error);
                    mMoviesPop.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mMoviesPop.postValue(null);
            }

            if (cancelRequest){
                return;
            }
        }

        // search method query
        private Call<MovieSearchResponse> getPopularMovies(int pageNumber){
            return Service.getMovieApi().getPopularMovies(Credentials.API_KEY, pageNumber);
        }

        private void cancelRequest(){
            Log.v("Tag", "Cancelling Search Request");
            cancelRequest = true;
        }
    }
}
