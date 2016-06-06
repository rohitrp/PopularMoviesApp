package io.github.rohitrp.popularmovies;

public class Movie {
    private String mTitle;
    private String mPosterUrl;
    private String mSynopsis;
    private double mRatings;
    private String mReleaseDate;
    private boolean mIsAdult;

    public Movie(String title, String posterUrl,
                 String synopsis, double ratings, String releaseDate,
                 boolean isAdult) {
        this.mTitle = title;
        this.mPosterUrl = posterUrl;
        this.mSynopsis = synopsis;
        this.mRatings = ratings;
        this.mReleaseDate = releaseDate;
        this.mIsAdult = isAdult;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPosterUrl() {
        return mPosterUrl;
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
}
