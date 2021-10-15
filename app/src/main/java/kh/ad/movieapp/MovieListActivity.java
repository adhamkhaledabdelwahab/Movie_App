package kh.ad.movieapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import kh.ad.movieapp.adapters.MovieRecyclerView;
import kh.ad.movieapp.adapters.onMovieListener;
import kh.ad.movieapp.models.MovieModel;
import kh.ad.movieapp.utils.Credentials;
import kh.ad.movieapp.viewmodels.MovieListViewModel;
import spork.Spork;
import spork.android.BindClick;
import spork.android.BindLayout;
import spork.android.BindView;

@SuppressLint("NonConstantResourceId")
@BindLayout(R.layout.activity_main)
public class MovieListActivity extends AppCompatActivity implements onMovieListener{

    // Before we run out app we need to add network security config

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.tool_bar)
    Toolbar toolBar;

    @BindView(R.id.search_view)
    SearchView searchView;

    MovieRecyclerView adapter;

    private MovieListViewModel movieListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Spork.bind(this);

        // Set the app language to english
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Configuration config;
            config = new Configuration(getResources().getConfiguration());
            config.locale = Locale.ENGLISH;
            config.setLayoutDirection(new Locale("en"));
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }

        setSupportActionBar(toolBar);

        setUpSearchView();

        movieListViewModel = new ViewModelProvider(this)
                .get(MovieListViewModel.class);

        configureRecyclerView();

        // calling the observer
        observeAnyChange();

        observePopularMovies();

        // Getting the data for popular movies
        movieListViewModel.searchPopularMoviesApi(1);
    }

    private void observePopularMovies(){
        movieListViewModel.getPopmMovies().observe(this,
                movieModels -> {
                    // Observing for any data change
                    if (movieModels != null){
                        Log.v("Tag","list length " + movieModels.size());
                        adapter.setmMovies(movieModels);
//                        for (MovieModel model : movieModels){
//                            // Get the data in log
//                            Log.v("Tag", "onChanged: " + model.getRelease_date());
//                        }
                    }
                });
    }

    // Get data from user  & query api to get results (movies)
    private void setUpSearchView(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.setmMovies(new ArrayList<>());
                // 4- calling method in mainActivity
                // displaying only the results of page 1
                movieListViewModel.searchMovieApi(query,1);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                adapter.setmMovies(new ArrayList<>());
//                // 4- calling method in mainActivity
//                // displaying only the results of page 1
//                movieListViewModel.searchMovieApi(newText,1);
                return false;
            }
        });
        searchView.setOnCloseListener(() -> {
            adapter.setmMovies(new ArrayList<>());
            // 4- calling method in mainActivity
            // displaying only the results of page 1
            movieListViewModel.searchPopularMoviesApi(1);
            Credentials.POPULAR = true;
            return false;
        });
    }

    @BindClick(R.id.search_view)
    private void onSearchClick(){
        Credentials.POPULAR = false;
    }
    
    // Observing any data change
    private void observeAnyChange(){
        movieListViewModel.getmMovies().observe(this,
                movieModels -> {
                    // Observing for any data change
                    if (movieModels != null){
                        adapter.setmMovies(movieModels);
//                        for (MovieModel model : movieModels){
//                            // Get the data in log
//                            Log.v("Tag", "onChanged: " + model.getRelease_date());
//                        }
                    }
                });
    }

    // 5- initializing recyclerview & adding data to it
    private void configureRecyclerView(){
        // Live data can't be passed via constructor
        adapter = new MovieRecyclerView(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollHorizontally(1))
                    Log.v("Tag", "" + Credentials.POPULAR);
                {
                    if (!Credentials.POPULAR) {
                        // here we need display next page
                        movieListViewModel.searchNextPage();
                    }
                    if (Credentials.POPULAR) {
                        movieListViewModel.searchNextPagePopular();
                    }
                }
            }
        });
    }

    @Override
    public void onMovieClick(int position) {
        Intent intent = new Intent(MovieListActivity.this,
                MovieDetailsActivity.class);
        intent.putExtra("movie", adapter.getSelectedMovie(position));
        startActivity(intent);
    }

    @Override
    public void onCategoryClick(String category) {

    }

}