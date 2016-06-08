package io.github.rohitrp.popularmovies;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
                .inflate(R.layout.movie_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.movie_dialog);
                dialog.setTitle(mMovies.get(holder.getAdapterPosition()).getTitle());

                TextView textView = (TextView) dialog.findViewById(R.id.dialog_text_view);
                textView.setText(mMovies.get(holder.getAdapterPosition())
                    .getTitle());

                Typeface openSansCondensedLight = Typeface.createFromAsset(
                        mContext.getAssets(), "fonts/OpenSans-CondLight.ttf");

                textView.setTypeface(openSansCondensedLight);

                ImageView imageView = (ImageView) dialog.findViewById(R.id.dialog_poster);
                Picasso.with(dialog.getContext())
                        .load(mMovies.get(holder.getAdapterPosition())
                            .getPosterUrl(Movie.POSTER_SIZE_LARGE))
                        .fit()
                        .placeholder(Movie.LOADING_PLACEHOLDER)
                        .into(imageView);
                dialog.show();
                return true;
            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                Intent intent = new Intent(context, MovieDetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, mMovies.get(position));
                context.startActivity(intent);

            }
        });

        Picasso.with(holder.mImageView.getContext())
                .load(mMovies.get(position).getPosterUrl(Movie.POSTER_SIZE_NORMAL))
                .fit()
                .placeholder(Movie.LOADING_PLACEHOLDER)
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }
}
