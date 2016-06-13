PopularMoviesApp
=================
## Stage 1

This app uses [The Movie Database (TMDb)](https://www.themoviedb.org)
to fetch the most popular and top rated movies data.

## Building And Using The App

Follow these steps to build the app:

1. Get an API Key from TMDb.
2. Create a file with name "gradle.properties" (without quotes) in the root directory
3. Add the following in the file:

    ```
    org.gradle.jvmargs=-Xmx1536m
    MyTMDbApiKey="YOUR TMDb API KEY HERE"
    ```
4. Build the app

## Screenshots

#### Vertical Orientation
-------------------------
<img src="/screenshots/movie_main_activity_popular.png" width="45%">
<img src="/screenshots/movie_main_activity_toprated.png" width="45%">
<img src="/screenshots/movie_dialog.png" width="45%">
<img src="/screenshots/movie_detail_activity.png" width="45%">

#### Horizontal Orientation
---------------------------
<img src="/screenshots/horizontal_movie_main_activity_popular.png">
<img src="/screenshots/horizontal_movie_detail_activity.png">



