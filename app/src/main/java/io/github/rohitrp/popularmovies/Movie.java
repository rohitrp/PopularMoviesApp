package io.github.rohitrp.popularmovies;

public class Movie {
    private String mTitle;
    private String mPosterUrl;
    private String mSynopsis;
    private String mRatings;
    private String mReleaseDate;

    public Movie(String title, String posterUrl,
                 String synopsis, String ratings, String releaseDate) {
        this.mTitle = title;
        this.mPosterUrl = posterUrl;
        this.mSynopsis = synopsis;
        this.mRatings = ratings;
        this.mReleaseDate = releaseDate;
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

    public String getRatings() {
        return mRatings;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }
}
