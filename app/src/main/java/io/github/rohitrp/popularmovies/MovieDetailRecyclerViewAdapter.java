package io.github.rohitrp.popularmovies;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Rohit on 08-06-2016.
 */

public class MovieDetailRecyclerViewAdapter
        extends RecyclerView.Adapter<MovieDetailRecyclerViewAdapter.ViewHolder> {

    private final String LOG_TAG = MovieDetailRecyclerViewAdapter.class.getSimpleName();
    private List<Movie.MovieDetail> mMovieDetails;

    private final Typeface mOpenSansCondensedLight;

    private Context mContext;
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView mTitle;
        public final TextView mBody;

        public ViewHolder(View view) {
            super(view);
            this.mView = view;
            this.mTitle = (TextView) view.findViewById(R.id.movie_detail_title_cardview_rv);
            this.mBody = (TextView) view.findViewById(R.id.movie_detail_body_cardview_rv);
        }
    }

    public MovieDetailRecyclerViewAdapter(Context context,
                                          List<Movie.MovieDetail> movieDetails) {
        mMovieDetails = movieDetails;
        mContext = context;

        mOpenSansCondensedLight = Typeface.createFromAsset(
                context.getAssets(), "fonts/OpenSans-CondLight.ttf");
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
