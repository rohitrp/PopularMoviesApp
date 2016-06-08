package io.github.rohitrp.popularmovies;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_movie_detail, new MovieDetailFragment())
                    .commit();
        }
    }

    public static class MovieDetailFragment extends Fragment {
        private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

        private RecyclerView mRecyclerView;

        public MovieDetailFragment() {
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail_movie, container, false);

            Intent intent = getActivity().getIntent();
            Movie movie = intent.getParcelableExtra(Intent.EXTRA_TEXT);

            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_movie_detail_rv);
            setupRecyclerView(movie);

            // Movie title
            TextView titleView = (TextView) rootView.findViewById(R.id.movie_detail_title);
            titleView.setText(movie.getTitle());

            // Movie title's font
            Typeface openSansCondensedLight = Typeface.createFromAsset(
                    getActivity().getAssets(), "fonts/OpenSans-CondLight.ttf");
            titleView.setTypeface(openSansCondensedLight);

            // Backdrop image
            ImageView imageView = (ImageView) rootView.findViewById(R.id.movie_detail_poster);

            Picasso.with(getContext())
                    .load(movie.getBackdropUrl(Movie.POSTER_SIZE_EXTRA_LARGE))
                    .fit()
                    .placeholder(Movie.LOADING_PLACEHOLDER)
                    .into(imageView);

            return rootView;
        }

        private void setupRecyclerView(Movie movie) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(
                    mRecyclerView.getContext()));

            mRecyclerView.setAdapter(new MovieDetailRecyclerViewAdapter(
                    getActivity(), movie.getTitleBodyPairs()));
        }
    }
}
