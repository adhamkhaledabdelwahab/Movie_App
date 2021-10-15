package kh.ad.movieapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import kh.ad.movieapp.R;
import kh.ad.movieapp.models.MovieModel;
import kh.ad.movieapp.utils.Credentials;

public class MovieRecyclerView extends RecyclerView.Adapter<MovieRecyclerView.Holder>{

    private List<MovieModel> mMovies;
    private final onMovieListener listener;
    private static final int DISPLAY_POPULAR = 1;
    private static final int DISPLAY_SEARCH = 2;

    public MovieRecyclerView(onMovieListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == DISPLAY_SEARCH){
            view = inflater.inflate(R.layout.movie_list_item, parent, false);
        }else{
            view = inflater.inflate(R.layout.popular_movies_layout, parent, false);
        }
        return new Holder(view, listener);

    }

    @Override
    public void onBindViewHolder(@NonNull MovieRecyclerView.Holder holder, int position) {
        int view = getItemViewType(position);
        if (view == DISPLAY_SEARCH){
            holder.title.setText(mMovies.get(position).getTitle());
            holder.ratingBar.setNumStars(5);
            holder.ratingBar.setRating(mMovies.get(position).getVote_average()/2);
            Glide.with(holder.itemView.getContext())
                    .load("https://image.tmdb.org/t/p/original"
                            + mMovies.get(position).getPoster_path())
                    .into(holder.imageView);
        }else {
            holder.title.setText(mMovies.get(position).getTitle());
            holder.ratingBar.setNumStars(5);
            holder.ratingBar.setRating(mMovies.get(position).getVote_average()/2);
            Glide.with(holder.itemView.getContext())
                    .load("https://image.tmdb.org/t/p/original"
                            + mMovies.get(position).getPoster_path())
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (Credentials.POPULAR){
            return DISPLAY_POPULAR;
        }else {
            return DISPLAY_SEARCH;
        }
    }

    @Override
    public int getItemCount() {
        if (mMovies != null)
            return mMovies.size();
        return 0;
    }

    public void setmMovies(List<MovieModel> movieModels) {
        this.mMovies = movieModels;
        this.notifyDataSetChanged();
    }

    public MovieModel getSelectedMovie(int position){
        if (mMovies != null) {
            if (mMovies.size() > 0) {
                return mMovies.get(position);
            }
        }
        return null;
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        onMovieListener listener;

        TextView title;

        ImageView imageView;

        RatingBar ratingBar;

        public Holder(@NonNull View itemView, onMovieListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.movie_title);
            imageView = itemView.findViewById(R.id.movie_img);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            itemView.setOnClickListener(this);
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            listener.onMovieClick(getAdapterPosition());
        }
    }
}
