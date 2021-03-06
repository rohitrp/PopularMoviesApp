package io.github.rohitrp.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.rohitrp.popularmovies.tmdbmodel.Genres;
import io.github.rohitrp.popularmovies.tmdbmodel.Movie;
import io.github.rohitrp.popularmovies.tmdbmodel.TmdbMovies;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

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

        String actionBarTitle;


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

        // Base URL for both movie and its genres
        final String TMDB_BASE_URL =
                "http://api.themoviedb.org/3/";
        final String TMDB_API_KEY =
                "api_key";

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());

        final String sortingPref = sharedPreferences.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));

        // Create custom Gson for parsing TMDb's custom data
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gsonBuilder.setDateFormat("yyyy-MM-dd");
        Gson gson = gsonBuilder.create();

        // Client for Retrofit to intercep URL and add a query parameter
        // for TMDb's API key
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    HttpUrl url = request.url().newBuilder()
                            .addQueryParameter(TMDB_API_KEY, BuildConfig.TMDb_API_KEY)
                            .build();

                    request = request.newBuilder()
                            .url(url)
                            .build();

                    return chain.proceed(request);
                }})
                .build();

        RxJavaCallAdapterFactory rxAdapter =
                RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TMDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .addCallAdapterFactory(rxAdapter)
                .build();

        final TmdbService tmdbService = retrofit.create(TmdbService.class);

        Observable<Genres> genresObservable = tmdbService.getGenres();
        Observable<TmdbMovies> tmdbMoviesObservable =
                tmdbService.getMoviesData(sortingPref);

        // Zipping both observables to make sure list of genres is
        // available before customizing movies data
        Observable<Void> combinedGenresMoviesObservable =
                Observable.zip(genresObservable, tmdbMoviesObservable,
                        new Func2<Genres, TmdbMovies, Void>() {
                            @Override
                            public Void call(Genres genres, TmdbMovies tmdbMovies) {
                                HashMap<Integer, String> genresList =
                                        new HashMap<Integer, String>();

                                for (Genres.Genre genre : genres.getGenres()) {
                                    genresList.put(genre.getId(), genre.getName());
                                }

                                List<Movie> movies = tmdbMovies.getResults();

                                for (Movie movie : movies) {

                                    List<Integer> currMovieGenresIds = movie.getGenreIds();
                                    ArrayList<String> currMovieGenres = new ArrayList<String>();

                                    for (int genreId : currMovieGenresIds) {
                                        if (genresList.containsKey(genreId)) {
                                            currMovieGenres.add(genresList.get(genreId));
                                        }
                                    }

                                    movie.setGenresList(currMovieGenres);
                                }

                                mMoviesList = new ArrayList<Movie>(movies);

                                return null;
                            }
                        });

        combinedGenresMoviesObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {
                        setAdapter();
                    }

                    @Override
                    public void onError(Throwable e) {
                        displayFailureSnackbar(getView());
                    }

                    @Override
                    public void onNext(Void aVoid) {

                    }
                });
    }

    private void displayFailureSnackbar(View view) {
        String errorMsg = "Something went wrong. Seems like your internet is not working";
        Snackbar.make(view, errorMsg, Snackbar.LENGTH_LONG).show();
    }

    private void setAdapter() {
        mRecyclerView.setAdapter(new MovieRecyclerViewAdapter(getActivity(),
                mMoviesList));
    }
}
