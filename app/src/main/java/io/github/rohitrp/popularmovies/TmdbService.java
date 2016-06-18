package io.github.rohitrp.popularmovies;

import io.github.rohitrp.popularmovies.tmdbmodel.Genres;
import io.github.rohitrp.popularmovies.tmdbmodel.TmdbMovies;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface TmdbService {

    @GET("movie/{sort}")
    Observable<TmdbMovies> getMoviesData(@Path("sort") String sort);

    @GET("genre/movie/list")
    Observable<Genres> getGenres();

}
