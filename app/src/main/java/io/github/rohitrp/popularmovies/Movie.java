package io.github.rohitrp.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class Movie implements Parcelable {
    private String mTitle;
    private String mPosterPath;
    private String mSynopsis;
    private double mRatings;
    private String mReleaseDate;
    private boolean mIsAdult;
    private String mBackdropPath;
    private ArrayList<String> mGenres;

    // Constants that can be used when requesting poster of different
    // sizes.
    public static final String POSTER_SIZE_NORMAL = "w342";
    public static final String POSTER_SIZE_LARGE = "w500";
    public static final String POSTER_SIZE_EXTRA_LARGE = "w780";

    // Placeholder drawable
    public static final int LOADING_PLACEHOLDER = R.drawable.loading;

    public Movie(String title, String posterPath,
                 String synopsis, double ratings, String releaseDate,
                 boolean isAdult, String backdropPath, ArrayList<String> genresIds) {
        this.mTitle = title;
        this.mPosterPath = posterPath;
        this.mSynopsis = synopsis;
        this.mRatings = ratings;
        this.mReleaseDate = releaseDate;
        this.mIsAdult = isAdult;
        this.mBackdropPath = backdropPath;
        this.mGenres = new ArrayList<>(genresIds);
    }

    public Movie(Parcel parcel) {
        this.mTitle = parcel.readString();
        this.mPosterPath = parcel.readString();
        this.mSynopsis = parcel.readString();
        this.mRatings = parcel.readDouble();
        this.mReleaseDate = parcel.readString();

        boolean[] parcelableBoolArray = new boolean[1];
        parcel.readBooleanArray(parcelableBoolArray);
        this.mIsAdult = parcelableBoolArray[0];

        this.mBackdropPath = parcel.readString();

        this.mGenres = new ArrayList<>();
        parcel.readStringList(this.mGenres);
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPosterUrl(String posterSize) {
        return getUrl(mPosterPath, posterSize);
    }

    public String getBackdropUrl(String posterSize) {
        return getUrl(mBackdropPath, posterSize);
    }

    private String getUrl(String path, String posterSize) {
        final String TMDB_BASE_POSTER_URL = "http://image.tmdb.org/t/p/";
        return TMDB_BASE_POSTER_URL + posterSize + path;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public double getRatings() {
        return mRatings;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public boolean isAdult() {
        return mIsAdult;
    }

    /**
     * Helper method to get Title - Body pairs to use in RecyclerView
     * using Movie.MovieDetail helper class.
     *
     * @return List of Movie.MovieDetail
     */
    public List<Movie.MovieDetail> getTitleBodyPairs() {
        ArrayList<MovieDetail> movieDetails = new ArrayList<>();

        final String synopsisTitle = "Synopsis";
        final String genresTitle = "Genres";
        final String releaseDateTitle = "Release Date";
        final String ratingsTitle = "Ratings";
        final String isAdultTitle = "Is Adult?";

        movieDetails.add(new MovieDetail(synopsisTitle, mSynopsis));
        movieDetails.add(new MovieDetail(genresTitle, TextUtils.join(", ", mGenres)));
        movieDetails.add(new MovieDetail(releaseDateTitle, mReleaseDate));
        movieDetails.add(new MovieDetail(ratingsTitle,
                String.valueOf(mRatings)));
        movieDetails.add(new MovieDetail(isAdultTitle,
                mIsAdult ? "Yes" : "No"));

        return movieDetails;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mSynopsis);
        dest.writeDouble(mRatings);
        dest.writeString(mReleaseDate);
        dest.writeBooleanArray(new boolean[]{mIsAdult});
        dest.writeString(mBackdropPath);
        dest.writeStringList(mGenres);
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
}
