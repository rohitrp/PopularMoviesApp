package io.github.rohitrp.popularmovies;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.rohitrp.popularmovies.tmdbmodel.Movie;

public class MovieDetailRecyclerViewAdapter
        extends RecyclerView.Adapter<MovieDetailRecyclerViewAdapter.ViewHolder> {

    private final String LOG_TAG = MovieDetailRecyclerViewAdapter.class.getSimpleName();
    private List<Movie.MovieDetail> mMovieDetails;

    private final Typeface mOpenSansCondensedLight;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.movie_detail_title_cardview_rv) TextView mTitle;
        @BindView(R.id.movie_detail_body_cardview_rv) TextView mBody;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public MovieDetailRecyclerViewAdapter(Context context,
                                          List<Movie.MovieDetail> movieDetails) {
        mMovieDetails = movieDetails;

        mOpenSansCondensedLight = Typefaces.get(
                context,
                Typefaces.OPEN_SANS_CONDENSED_LIGHT_FONT);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_detail_movie_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String title = mMovieDetails.get(position).getTitle();
        final String body = mMovieDetails.get(position).getBody();

        holder.mTitle.setText(title);
        holder.mBody.setText(body);

        holder.mBody.setTypeface(mOpenSansCondensedLight);
    }

    @Override
    public int getItemCount() {
        return mMovieDetails.size();
    }
}
