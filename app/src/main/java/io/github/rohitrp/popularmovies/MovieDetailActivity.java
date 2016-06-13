package io.github.rohitrp.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieDetailActivity extends AppCompatActivity {
    private final String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private Movie mMovie;
    private MenuItem mShareMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();
        mMovie = intent.getParcelableExtra(Intent.EXTRA_TEXT);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.activity_movie_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.activity_movie_detail_collapsing_toolbar);
        collapsingToolbarLayout.setTitle(mMovie.getTitle());

        final ImageView backdropImageView = (ImageView) findViewById(R.id.activity_movie_detail_backdrop);
        Picasso.with(this)
                .load(mMovie.getBackdropUrl(Movie.POSTER_SIZE_EXTRA_LARGE))
                .fit()
                .centerCrop()
                .into(backdropImageView);

        mRecyclerView = (RecyclerView) findViewById(R.id.activity_movie_detail_rv);
        setupRecyclerView(mMovie);

        // Since there are two ways to share movie - Share menu and FAB button -
        // this makes sure that share menu is not visible when fab button is
        // visible, and vice versa.
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.activity_movie_detail_appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (mShareMenuItem != null) {

                    // Boolean to check is toolbar is collapsed
                    boolean showShareMenuItem =
                            collapsingToolbarLayout.getHeight() + verticalOffset <
                                    2 * ViewCompat.getMinimumHeight(collapsingToolbarLayout);

                    // If collapsed, show share menu item
                    mShareMenuItem.setVisible(showShareMenuItem);
                }
            }
        });

        FloatingActionButton shareFab =
                (FloatingActionButton) findViewById(R.id.activity_movie_detail_share_fab);

        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startShareActivity();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.moviedetailactivity, menu);
        mShareMenuItem = menu.findItem(R.id.menu_share);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_share) {
            startShareActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to create share intent for both
     * FloatingActionButton and Menu item
     */
    private void startShareActivity() {
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

    private void setupRecyclerView(Movie movie) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(
                mRecyclerView.getContext()));

        mRecyclerView.setAdapter(new MovieDetailRecyclerViewAdapter(
                this, movie.getTitleBodyPairs()));
    }

}
