package io.github.rohitrp.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private String mTitle;
    private String mPosterPath;
    private String mSynopsis;
    private double mRatings;
    private String mReleaseDate;
    private boolean mIsAdult;
    private String mBackdropPath;

    // Constants that can be used when requesting poster of different
    // sizes.
    public static final String POSTER_SIZE_NORMAL = "w342";
    public static final String POSTER_SIZE_LARGE = "w500";
    public static final String POSTER_SIZE_EXTRA_LARGE = "w780";

    // Placeholder drawable
    public static final int LOADING_PLACEHOLDER = R.drawable.ellipsis;

    public Movie(String title, String posterPath,
                 String synopsis, double ratings, String releaseDate,
                 boolean isAdult, String backdropPath) {
        this.mTitle = title;
        this.mPosterPath = posterPath;
        this.mSynopsis = synopsis;
        this.mRatings = ratings;
        this.mReleaseDate = releaseDate;
        this.mIsAdult = isAdult;
        this.mBackdropPath = backdropPath;
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mSynopsis);
        dest.writeDouble(mRatings);
        dest.writeString(mReleaseDate);
        dest.writeBooleanArray(new boolean[]{mIsAdult});
        dest.writeString(mPosterPath);
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
}
