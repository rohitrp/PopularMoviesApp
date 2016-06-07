package io.github.rohitrp.popularmovies;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MovieFragment extends Fragment {

    private final String LOG_TAG = MovieFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;

    public MovieFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) inflater.inflate(
                R.layout.fragment_movie, container, false);

        setupRecyclerView();
        return mRecyclerView;
    }

    private void setupRecyclerView() {

        // TODO: Try different number of columns for different orientations
        // (2 for vertical, 3 for Horizontal)
        mRecyclerView.setLayoutManager(new GridLayoutManager(
                mRecyclerView.getContext(), 2));

        mRecyclerView.setAdapter(new RecyclerViewAdapter(getActivity(),
                new ArrayList<Movie>()));
    }

    private void updateMovies() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());

        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();

        fetchMoviesTask.execute(sharedPreferences.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default)));
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {
        @Override
        protected Movie[] doInBackground(String... params) {

            if (params.length == 0) return null;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonStr = null;

            try {
                final String TMDB_BASE_URL =
                        "http://api.themoviedb.org/3/movie/";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDb_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing
            // movies.
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (movies != null) {
                mRecyclerView.setAdapter(new RecyclerViewAdapter(getActivity(),
                        new ArrayList<Movie>(Arrays.asList(movies))));
            }
        }

        private Movie[] getMoviesDataFromJson(String moviesJsonStr)
            throws JSONException {

            final String TMDB_RESULTS = "results";
            final String TMDB_POSTER_PATH = "poster_path";
            final String TMDB_ADULT = "adult";
            final String TMDB_SYNOPSIS = "overview";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_TITLE = "original_title";
            final String TMDB_RATINGS = "vote_average";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(TMDB_RESULTS);

            Movie[] resultMovies = new Movie[moviesArray.length()];

            for (int i = 0; i < moviesArray.length(); i++) {
                String title;
                String posterUrl;
                String synopsis;
                double ratings;
                String releaseDate;
                boolean isAdult;

                JSONObject currMovie = moviesArray.getJSONObject(i);

                title = currMovie.getString(TMDB_TITLE);
                posterUrl = currMovie.getString(TMDB_POSTER_PATH);
                synopsis = currMovie.getString(TMDB_SYNOPSIS);
                ratings = currMovie.getDouble(TMDB_RATINGS);
                releaseDate = currMovie.getString(TMDB_RELEASE_DATE);
                isAdult = currMovie.getBoolean(TMDB_ADULT);

                resultMovies[i] = new Movie(title, posterUrl, synopsis,
                        ratings, releaseDate, isAdult);
            }

            return resultMovies;
        }
    }
}
