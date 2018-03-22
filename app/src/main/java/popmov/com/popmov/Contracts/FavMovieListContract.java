package popmov.com.popmov.Contracts;

import android.provider.BaseColumns;

/**
 * Created by ankeshkatiyar on 25/02/18.
 */

public class FavMovieListContract {
    public  class FavMovieEntry implements BaseColumns{
        public static final String TABLE_NAME = "favmovies";
        public static final String COULUMN_MOVIE_ID = "movieid";
        public static final String COULUMN_MOVIE_NAME = "name";
        public static final String COULUMN_RELEASE_DATE = "date";
        public static final String COULUMN_RATING = "rating";
        public static final String COULUMN_SUMMARY = "summary";
        public static final String COULUMN_MOVIE_IMAGE = "image";


    }
    public  class FavMovieTrailersEntry implements BaseColumns{

        public static final String TABLE_NAME = "movietrailers";
        public static final String COULUMN_ID = "id";
        public static final String COULUMN_MOVIE_ID = "movieid";
        public static final String COULUMN_TRAILERS = "trailersid";

    }
    public  class FavMovieReviewsEntry implements  BaseColumns{
        public static final String TABLE_NAME = "moviereviews";
        public static final String COULUMN_ID = "id";
        public static final String COULUMN_MOVIE_ID = "movieid";
        public static final String COULUMN_AUTHOR = "author";
        public static final String COULUMN_REVIEW = "review";
        public static final String COULUMN_URL = "url";
    }
}
