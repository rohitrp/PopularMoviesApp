package io.github.rohitrp.popularmovies;

import io.github.rohitrp.popularmovies.tmdbmodel.Genres;
import io.github.rohitrp.popularmovies.tmdbmodel.TmdbMovies;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TmdbService {

    @GET("movie/{sort}")
    Call<TmdbMovies> getMoviesData(@Path("sort") String sort);

    @GET("genre/movie/list")
    Call<Genres> getGenres();

}
