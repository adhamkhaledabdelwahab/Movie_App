package kh.ad.movieapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import kh.ad.movieapp.models.MovieModel;
import kh.ad.movieapp.viewmodels.MovieListViewModel;
import spork.Spork;
import spork.android.BindClick;
import spork.android.BindLayout;
import spork.android.BindView;

@SuppressLint("NonConstantResourceId")
@BindLayout(R.layout.activity_movie_details)
public class MovieDetailsActivity extends AppCompatActivity {

    @BindView(R.id.movie_details_title)
    TextView title;

    @BindView(R.id.movie_details_desc)
    TextView desc;

    @BindView(R.id.movie_details_image)
    ImageView posterImage;

    @BindView(R.id.movie_details_rating)
    RatingBar rating;

    private MovieListViewModel movieListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Spork.bind(this);

        movieListViewModel = new ViewModelProvider(this).get(MovieListViewModel.class);

        GetDataFromIntent();
    }

    private void GetDataFromIntent() {
        if (getIntent().hasExtra("movie")){
            MovieModel model = getIntent().getParcelableExtra("movie");
            Log.v("Tag", "incoming intent " + model.getId());
            desc.setText(model.getOverview());
            title.setText(model.getTitle());
            rating.setRating(model.getVote_average()/2);
            Glide.with(this)
                    .load("https://image.tmdb.org/t/p/original"
                            + (model.getBackdrop_path() != null ? model.getBackdrop_path() :
                            model.getPoster_path()))
                    .into(posterImage);
        }
    }

    @BindClick(R.id.backButton)
    private void backButton() {
        onBackPressed();
    }

}