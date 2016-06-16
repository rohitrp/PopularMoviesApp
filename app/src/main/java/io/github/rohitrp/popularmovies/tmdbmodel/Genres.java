package io.github.rohitrp.popularmovies.tmdbmodel;

import java.util.List;

public class Genres {
    private List<Genre> genres;

    public List<Genre> getGenres() {
        return genres;
    }

    public class Genre {
        private Integer id;
        private String name;

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return id + ": " + name;
        }
    }
}
