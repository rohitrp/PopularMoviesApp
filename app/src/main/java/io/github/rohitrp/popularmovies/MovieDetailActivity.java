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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

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
        private Movie mMovie;

        public MovieDetailFragment() {
            setHasOptionsMenu(true);
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
            mMovie = intent.getParcelableExtra(Intent.EXTRA_TEXT);

            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_movie_detail_rv);
            setupRecyclerView(mMovie);

            // Movie title
            TextView titleView = (TextView) rootView.findViewById(R.id.movie_detail_title);
            titleView.setText(mMovie.getTitle());

            // Movie title's font
            Typeface openSansCondensedLight = Typeface.createFromAsset(
                    getActivity().getAssets(), "fonts/OpenSans-CondLight.ttf");
            titleView.setTypeface(openSansCondensedLight);

            // Backdrop image
            ImageView imageView = (ImageView) rootView.findViewById(R.id.movie_detail_poster);

            Picasso.with(getContext())
                    .load(mMovie.getBackdropUrl(Movie.POSTER_SIZE_EXTRA_LARGE))
                    .fit()
                    .placeholder(Movie.LOADING_PLACEHOLDER)
                    .into(imageView);

            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.moviedetailfragment, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.menu_share) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");

                String shareMessage = "Title:\n" + mMovie.getTitle();
                List<Movie.MovieDetail> movieDetails =
                        mMovie.getTitleBodyPairs();

                for (Movie.MovieDetail detail : movieDetails) {
                    shareMessage += "\n\n" + detail.getTitle() + ":\n" +
                            detail.getBody();
                }

                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

                startActivity(Intent.createChooser(shareIntent, getString(R.string.menu_share)));
            }

            return true;
        }


        private void setupRecyclerView(Movie movie) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(
                    mRecyclerView.getContext()));

            mRecyclerView.setAdapter(new MovieDetailRecyclerViewAdapter(
                    getActivity(), movie.getTitleBodyPairs()));
        }
    }
}
