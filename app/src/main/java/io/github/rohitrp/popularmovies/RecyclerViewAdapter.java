package io.github.rohitrp.popularmovies;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Movie> mMovies;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.movie_poster);
        }
    }

    public RecyclerViewAdapter(Context context, List<Movie> movies) {
        mMovies = movies;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_card_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Snackbar.make(v, mMovies.get(position).getTitle(), Snackbar.LENGTH_SHORT)
                        .show();
            }
        });

        Picasso.with(holder.mImageView.getContext())
                .load(mMovies.get(position).getPosterUrl())
                .fit()
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }
}
