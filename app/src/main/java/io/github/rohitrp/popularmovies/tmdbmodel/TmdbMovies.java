package io.github.rohitrp.popularmovies.tmdbmodel;

import java.util.List;

public class TmdbMovies {
    private Integer page;
    private List<Movie> results;
    private Integer totalResults;
    private Integer totalPages;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<Movie> getResults() {

        return results;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public Integer getTotalPages() {
        return totalPages;
    }
}
