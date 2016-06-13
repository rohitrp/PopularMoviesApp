package io.github.rohitrp.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

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
import java.util.HashMap;

public class MovieFragment extends Fragment {

    private final String LOG_TAG = MovieFragment.class.getSimpleName();

    // Keys used to store and retrieve saved instance states
    private final String PREF_KEY = "sort_pref";
    private final String MOVIES_KEY = "movies";

    private RecyclerView mRecyclerView;

    private ArrayList<Movie> mMoviesList;

    public MovieFragment() {
        mMoviesList = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null
                || !savedInstanceState.containsKey(MOVIES_KEY)) {
            updateMovies();
        } else {
            mMoviesList = savedInstanceState.getParcelableArrayList("movies");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIES_KEY, mMoviesList);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String pref = prefs.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));

        // Save the current sorting preference to compare it with the
        // sorting preference when the activity resumes
        getActivity().getIntent().putExtra(PREF_KEY, pref);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String currentPref = prefs.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));

        Intent intent = getActivity().getIntent();

        // Check if the sorting preference when the activity was stopped is
        // same as the current sorting preference. If it is not, fetch data
        // with the new sorting preference.
        if (intent.hasExtra(PREF_KEY)
                && !intent.getStringExtra(PREF_KEY).equals(currentPref)) {
            updateMovies();
        }

        String actionBarTitle = "";


        // TODO: Try to find a better way to implement this
        // Keys and values of ListPreference

        // Keys
        final String popularKey = getString(R.string.pref_sort_key_popular);
        final String topRatedKey = getString(R.string.pref_sort_key_toprated);
        final String nowPlayingKey = getString(R.string.pref_sort_key_nowplaying);
        final String upcomingKey = getString(R.string.pref_sort_key_upcoming);

        // Values
        final String popularValue = getString(R.string.pref_sort_value_popular);
        final String topRatedValue = getString(R.string.pref_sort_value_toprated);
        final String nowPlayingValue = getString(R.string.pref_sort_value_nowplaying);
        final String upcomingValue = getString(R.string.pref_sort_value_upcoming);

        if (currentPref.equals(popularValue)) {
            actionBarTitle = popularKey;
        } else if (currentPref.equals(topRatedValue)) {
            actionBarTitle = topRatedKey;
        } else if (currentPref.equals(nowPlayingValue)) {
            actionBarTitle = nowPlayingKey;
        } else {
            actionBarTitle = upcomingKey;
        }

        // Change title of ActionBar
        ((MainActivity) getActivity()).setActionBarTitle(actionBarTitle);
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

        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();

        int orientation = display.getRotation();
        int cols = 2;           // In vertical orientation, number of columns = 2

        // Check if orientation is horizontal. If it is, change number
        // of columns to 3
        if (orientation == Surface.ROTATION_90 ||
                orientation == Surface.ROTATION_270) {
            cols = 3;
        }

        mRecyclerView.setLayoutManager(new GridLayoutManager(
                mRecyclerView.getContext(), cols));

        mRecyclerView.setAdapter(new MovieRecyclerViewAdapter(getActivity(),
                new ArrayList<Movie>()));

        setAdapter();
    }

    private void updateMovies() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());

        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();

        fetchMoviesTask.execute(sharedPreferences.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default)));
    }

    private void setAdapter() {
        mRecyclerView.setAdapter(new MovieRecyclerViewAdapter(getActivity(),
                mMoviesList));
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {
        @Override
        protected Movie[] doInBackground(String... params) {

            if (params.length == 0) return null;

            HttpURLConnection urlConnection = null;

            String moviesJsonStr = null;
            String genresJsonStr = null;

            try {

                // Base URL for both movie and its genres
                final String TMDB_BASE_URL =
                        "http://api.themoviedb.org/3/";

                // TMDb's API key
                final String API_KEY_PARAM = "api_key";

                // URL to fetch movie data
                Uri moviesBuiltUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                        .appendPath("movie")
                        .appendPath(params[0])
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDb_API_KEY)
                        .build();

                URL moviesUrl = new URL(moviesBuiltUri.toString());

                // URL to fetch movie genres
                Uri genresBuiltUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                        .appendPath("genre")
                        .appendPath("movie")
                        .appendPath("list")
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDb_API_KEY)
                        .build();

                URL genresUrl = new URL(genresBuiltUri.toString());

                // Get movies data
                urlConnection = (HttpURLConnection) moviesUrl.openConnection();
                moviesJsonStr = fetchJsonData(urlConnection);

                // Get genres data
                urlConnection = (HttpURLConnection) genresUrl.openConnection();
                genresJsonStr = fetchJsonData(urlConnection);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            try {
                return getMoviesDataFromJson(moviesJsonStr, genresJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing
            // movies.
            return null;
        }

        /**
         * Helper method to fetch data from the provided url
         * @param urlConnection URL connection opened with the url from
         *                      which data is to be fetched
         * @return JSON data received from the url connection
         * @throws IOException
         */
        private String fetchJsonData(HttpURLConnection urlConnection)
                throws IOException {

            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }

            // Return the JSON received from the url
            return buffer.toString();
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (movies != null) {
                mMoviesList = new ArrayList<>(Arrays.asList(movies));
                setAdapter();
            }
        }

        private Movie[] getMoviesDataFromJson(String moviesJsonStr, String genresJsonStr)
            throws JSONException {

            final String TMDB_RESULTS = "results";
            final String TMDB_POSTER_PATH = "poster_path";
            final String TMDB_ADULT = "adult";
            final String TMDB_SYNOPSIS = "overview";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_TITLE = "original_title";
            final String TMDB_RATINGS = "vote_average";
            final String TMDB_BACKDROP_PATH = "backdrop_path";
            final String TMDB_GENRES_IDS = "genre_ids";

            final String TMDB_GENRES = "genres";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(TMDB_RESULTS);

            JSONObject genresJson = new JSONObject(genresJsonStr);
            JSONArray genresArray = genresJson.getJSONArray(TMDB_GENRES);

            Movie[] resultMovies = new Movie[moviesArray.length()];
            HashMap<Integer, String> genresList = new HashMap<>();

            // Store list of genres
            for (int i = 0; i < genresArray.length(); i++) {
                JSONObject genre = genresArray.getJSONObject(i);
                genresList.put(genre.getInt("id"), genre.getString("name"));
            }

            for (int i = 0; i < moviesArray.length(); i++) {
                String title;
                String posterPath;
                String synopsis;
                double ratings;
                String releaseDate;
                boolean isAdult;
                String backdropPath;
                JSONArray genresIdJsonArray;
                ArrayList<String> genresIds = new ArrayList<>();

                JSONObject currMovie = moviesArray.getJSONObject(i);

                title = currMovie.getString(TMDB_TITLE);
                posterPath = currMovie.getString(TMDB_POSTER_PATH);
                synopsis = currMovie.getString(TMDB_SYNOPSIS);
                ratings = currMovie.getDouble(TMDB_RATINGS);
                releaseDate = currMovie.getString(TMDB_RELEASE_DATE);
                isAdult = currMovie.getBoolean(TMDB_ADULT);
                backdropPath = currMovie.getString(TMDB_BACKDROP_PATH);

                genresIdJsonArray = currMovie.getJSONArray(TMDB_GENRES_IDS);

                for (int j = 0; j < genresIdJsonArray.length(); j++) {
                    genresIds.add(genresList.get(genresIdJsonArray.getInt(j)));
                }

                resultMovies[i] = new Movie(title, posterPath, synopsis,
                        ratings, releaseDate, isAdult, backdropPath, genresIds);
            }

            return resultMovies;
        }
    }
}
