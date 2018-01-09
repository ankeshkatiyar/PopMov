package popmov.com.popmov;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


class MoviesJsonUtils {

    private final static String ERROR_CODE = "status_code";
    private final static int INVALID_API_KEY = 7;
    private final static int RESOURCE_NOT_FOUND = 34;
    private final static String MOVIE_NAME = "title";
    private final static String MOVIE_SUMMARY = "overview";
    private final static String MOVIE_RATING = "vote_average";
    private final static String MOVIE_RELEASE_DATE = "release_date";
    private final static String MOVIE_POSTER_PATH = "poster_path";
    private final static String MOVIE_RESULTS = "results";
    private final static String TOTAL_PAGES = "total_pages";
    private static int mTotalNumberOfPages = -1;


    static ArrayList<MoviesModel> getMoviesDataFromJSON(String JSONString) {
        String mMovieName;
        String mMovieOverview;
        String mMovieRating;
        String mMovieReleaseDate;
        String mMoviePosterPath;

        ArrayList<MoviesModel> moviesDataList = new ArrayList<>();


        try {
            JSONObject jsonObject = new JSONObject(JSONString);

            if (jsonObject.has(ERROR_CODE)) {
                int errorCode = jsonObject.getInt(ERROR_CODE);
                switch (errorCode) {
                    case INVALID_API_KEY:
                        Log.e("API_ERROR", "Invalid API Key");
                        break;
                    case RESOURCE_NOT_FOUND:
                        Log.e("API_ERROR", "Resource Not found");
                        break;
                    default:
                        Log.e("API_ERROR", "Internal Server Error");

                }
                return null;
            } else {

                JSONArray moviesArrayJSON = jsonObject.getJSONArray(MOVIE_RESULTS);

                int numberOfMovies = moviesArrayJSON.length();
                for (int i = 0; i < numberOfMovies; i++) {

                    JSONObject singleMovieJSONObject = moviesArrayJSON.getJSONObject(i);
                    mMovieName = singleMovieJSONObject.getString(MOVIE_NAME);
                    mMovieOverview = singleMovieJSONObject.getString(MOVIE_SUMMARY);
                    mMovieRating = Double.toString(singleMovieJSONObject.getDouble(MOVIE_RATING));
                    mMovieReleaseDate = singleMovieJSONObject.getString(MOVIE_RELEASE_DATE);
                    mMoviePosterPath = singleMovieJSONObject.getString(MOVIE_POSTER_PATH);
                    moviesDataList.add(new MoviesModel(mMovieName, mMovieOverview, mMovieRating, mMovieReleaseDate, mMoviePosterPath));
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return moviesDataList;
    }
}
