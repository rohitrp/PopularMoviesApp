package io.github.rohitrp.popularmovies.tmdbmodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.github.rohitrp.popularmovies.R;

public class Movie implements Parcelable {
    private final String LOG_TAG = Movie.class.getSimpleName();

    private String posterPath;
    private Boolean adult;
    private String overview;
    private Date releaseDate;
    private ArrayList<Integer> genreIds;
    private Long id;
    private String originalTitle;
    private String originalLanguage;
    private String title;
    private String backdropPath;
    private Double popularity;
    private Long voteCount;
    private Boolean video;
    private Double voteAverage;
    private ArrayList<String> genresList;

    // Constants that can be used when requesting poster of different
    // sizes.
    public static final String POSTER_SIZE_NORMAL = "w342";
    public static final String POSTER_SIZE_LARGE = "w500";
    public static final String POSTER_SIZE_EXTRA_LARGE = "w780";
    public static final String POSTER_SIZE_ORIGINAL = "original";

    // Placeholder drawable
    public static final int LOADING_PLACEHOLDER = R.drawable.loading;

    public Movie(Parcel parcel) {
        this.title = parcel.readString();
        this.posterPath = parcel.readString();
        this.overview = parcel.readString();
        this.voteAverage = parcel.readDouble();
        this.releaseDate = (Date) parcel.readSerializable();

        boolean[] parcelableBoolArray = new boolean[1];
        parcel.readBooleanArray(parcelableBoolArray);
        this.adult = parcelableBoolArray[0];

        this.backdropPath = parcel.readString();

        this.genresList = new ArrayList<>();
        parcel.readStringList(this.genresList);
    }


    public String getPosterUrl(String posterSize) {
        return getUrl(posterPath, posterSize);
    }

    public String getBackdropUrl(String posterSize) {
        return getUrl(backdropPath, posterSize);
    }

    private String getUrl(String path, String posterSize) {
        final String TMDB_BASE_POSTER_URL = "http://image.tmdb.org/t/p/";
        return TMDB_BASE_POSTER_URL + posterSize + path;
    }

    /**
     * Helper method to get Title - Body pairs to use in RecyclerView
     * using Movie.MovieDetail helper class.
     *
     * @return List of Movie.MovieDetail
     */
    public List<Movie.MovieDetail> getTitleBodyPairs() {
        ArrayList<Movie.MovieDetail> movieDetails = new ArrayList<>();

        final String synopsisTitle = "Synopsis";
        final String genresTitle = "Genres";
        final String releaseDateTitle = "Release Date";
        final String ratingsTitle = "Ratings";
        final String isAdultTitle = "Is Adult?";

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");

        Log.d(LOG_TAG, genresList.toString());
        movieDetails.add(new MovieDetail(synopsisTitle, overview));
        movieDetails.add(new MovieDetail(genresTitle, TextUtils.join(", ", genresList)));
        movieDetails.add(new MovieDetail(releaseDateTitle, dateFormat.format(releaseDate)));
        movieDetails.add(new MovieDetail(ratingsTitle,
                String.valueOf(voteAverage) + " / 10"));
        movieDetails.add(new MovieDetail(isAdultTitle,
                adult ? "Yes" : "No"));

        return movieDetails;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeDouble(voteAverage);
        dest.writeSerializable(releaseDate);
        dest.writeBooleanArray(new boolean[]{adult});
        dest.writeString(backdropPath);
        dest.writeStringList(genresList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    /**
     * A helper class to store movie details as
     * key (Title) - value (Body) pair.
     *
     * Example use -
     * 1) mTitle - Synopsis
     *    mBody - The synopsis (or description) of the movie.
     *
     * 2) mTitle - Release Date
     *    mBody - The release date of movie.
     *
     * The class is used in the RecyclerView for the MovieDetailActivity.
     */
    public static class MovieDetail {
        private final String mTitle;
        private final String mBody;

        public MovieDetail(String title, String body) {
            this.mTitle = title;
            this.mBody = body;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getBody() {
            return mBody;
        }

    }

    public ArrayList<String> getGenresList() {
        return genresList;
    }

    public void setGenresList(ArrayList<String> genresList) {
        this.genresList = new ArrayList<>(genresList);
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public Boolean getAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public ArrayList<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(ArrayList<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public Long getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Long voteCount) {
        this.voteCount = voteCount;
    }

    public Boolean getVideo() {
        return video;
    }

    public void setVideo(Boolean video) {
        this.video = video;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }
}
