package popmov.com.popmov;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieListCLickListener {


    private static final String MOVIE_DETAILS = "movie_details";
    private MoviesAdapter moviesAdapter;
    private static final int NUMBER_OF_MOVIES = 5;
    private static int mTotalPages = -1;
    private static int currentPageNumber = 1;
    private Toast toast;
    private final static String MOVIE_TITLE = "name";
    private final static String MOVIE_OVERVIEW = "overview";
    private final static String MOVIE_RATING = "rating";
    private final static String MOVIE_RELEASE_DATE = "date";
    private final static String MOVIE_POSTER_URL = "url";
    private final static String POPULAR_MOVIES = "popular";
    private final static String TOP_RATED_MOVIES = "top_rated";
    private final static String TOTAL_PAGES = "total_pages";
    private static String movieListCategory = POPULAR_MOVIES;
    private String currentListCategory = POPULAR_MOVIES;
    private boolean isCancelled = false;
    private int numberOfApiCalls;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView moviesRecyclerView;


        moviesRecyclerView = findViewById(R.id.rv_top_movies);
        moviesAdapter = new MoviesAdapter(NUMBER_OF_MOVIES, this, new MoviesAdapter.MoviePosition() {

            /*This method is there to call asynctask again when user is about to reach the end of
               the scrolling
           */
            @Override
            public void currentPosition(int position, int totalItems) {

                if (position == totalItems - 20) {

                    new FetchMoviesTask().execute(movieListCategory);
                    numberOfApiCalls = 1;
                }
            }
        });
        if (isNetworkAvailable()) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
            moviesRecyclerView.setLayoutManager(gridLayoutManager);
            moviesRecyclerView.setHasFixedSize(true);
            moviesRecyclerView.setAdapter(moviesAdapter);
            new FetchMoviesTask().execute(POPULAR_MOVIES);
        } else {

            noInternetConnection();
        }

    }

    //resetting the page number of the API call if user exits the activity
    @Override
    protected void onStop() {
        super.onStop();
        currentPageNumber = 1;
    }

    //Method to get  details of a movie on a click
    @Override
    public void onMovieClick(MoviesModel moviesModel) {
        Intent intent = new Intent(this, MovieDetails.class);
        intent.putExtra(MOVIE_DETAILS, (Parcelable) moviesModel);

        startActivity(intent);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sort, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.top_movies:
                if (!movieListCategory.equals(TOP_RATED_MOVIES)) {
                    isCancelled = true;
                    movieListCategory = TOP_RATED_MOVIES;
                    /*If the ASYNC task is running then it will be cancelled and will be called within
                       OnCancelled method.But if it is not running then it has to be called from here
                     */

                    if (new FetchMoviesTask().getStatus() != AsyncTask.Status.RUNNING) {
                        new FetchMoviesTask().execute(movieListCategory);
                    }

                }
                break;

            case R.id.popular_movies:
                if (!movieListCategory.equals(POPULAR_MOVIES)) {
                    isCancelled = true;
                    movieListCategory = POPULAR_MOVIES;
                    if (new FetchMoviesTask().getStatus() != AsyncTask.Status.RUNNING) {
                        new FetchMoviesTask().execute(movieListCategory);
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void noInternetConnection() {
        Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show();

    }

    //Async Task to fetch the movie data from the API
    public class FetchMoviesTask extends AsyncTask<String, ArrayList<MoviesModel>, ArrayList<MoviesModel>> {


        private ArrayList<MoviesModel> mMovieDetailsArrayList = new ArrayList<>();

        @Override
        protected void onPreExecute() {

            //clearing all the items in adapter when user changes the sort method
            //currentListCategory!=movieListCategory is required to check if the user has changed the sort type or we are calling
            //async task again with the same category

            if (moviesAdapter.getItemCount() != 0 && !currentListCategory.equals(movieListCategory)) {
                moviesAdapter.removeAllItems();
                mTotalPages = -1;
                currentPageNumber = 1;
            }

        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onProgressUpdate(ArrayList<MoviesModel>... values) {
            if (values != null) {
                moviesAdapter.addItem(values[0]);
            }

        }

        @Override
        protected void onCancelled() {
            isCancelled = false;
            new FetchMoviesTask().execute(movieListCategory);

        }


        @Override
        protected ArrayList<MoviesModel> doInBackground(String... moviesListCategory) {

            numberOfApiCalls = 1;
            currentListCategory = moviesListCategory[0];
            if (isNetworkAvailable()) {
                //Calling only 5 pages at a time to reduce the number of API calls
                while (numberOfApiCalls <= 5) {


                    if (isCancelled) {

                        cancel(true);
                        return null;
                    }
                    String movieData;

                    try {

                        movieData = NetworkUtils.getResponseFromHttp(NetworkUtils.buildURL(currentPageNumber, currentListCategory));
                        mMovieDetailsArrayList = MoviesJsonUtils.getMoviesDataFromJSON(movieData);
                        if (mTotalPages == -1) {
                            JSONObject jsonObject = new JSONObject(movieData);
                            mTotalPages = jsonObject.getInt(TOTAL_PAGES);
                        }
                        publishProgress(mMovieDetailsArrayList);
                        currentPageNumber++;
                        if (currentPageNumber > mTotalPages) {
                            Toast.makeText(getApplicationContext(), R.string.end_reached, Toast.LENGTH_LONG).show();
                            break;

                        }
                        numberOfApiCalls++;

                    } catch (IOException | JSONException ie) {
                        ie.printStackTrace();
                    }
                }


            } else {
                noInternetConnection();
            }
            return mMovieDetailsArrayList;
        }

    }


}
