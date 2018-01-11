package popmov.com.popmov;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class FetchMoviesTask extends AsyncTask<String, ArrayList<MoviesModel>, ArrayList<MoviesModel>> {

    private static int mTotalPages = -1;
    static int currentPageNumber = 1;
    private Toast toast;
    private final static String POPULAR_MOVIES = "popular";
    private final static String TOP_RATED_MOVIES = "top_rated";
    private final static String TOTAL_PAGES = "total_pages";
    private String movieListCategory = POPULAR_MOVIES;
    private static String currentListCategory = POPULAR_MOVIES;
    static boolean isCancelled = false;
    private int numberOfApiCalls = 1;
    private Context context = null;
    private MoviesAsyncTaskCompleteListener movieAsyncTaskCompleteListener;
    private ArrayList<MoviesModel> mMovieDetailsArrayList = new ArrayList<>();

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    FetchMoviesTask(Context context, MoviesAsyncTaskCompleteListener<ArrayList<MoviesModel>> listener,String movieListCategory) {
        this.context = context;
        this.movieAsyncTaskCompleteListener = listener;
        numberOfApiCalls = 1;
        this.movieListCategory = movieListCategory;

    }


    @Override
    protected void onPreExecute() {

        //clearing all the items in adapter when user changes the sort method
        //currentListCategory!=movieListCategory is required to check if the user has changed the sort type or we are calling
        //async task again with the same category

        if (!currentListCategory.equals(movieListCategory)) {
            movieAsyncTaskCompleteListener.onRemoveItems();
            mTotalPages = -1;
            currentPageNumber = 1;
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onProgressUpdate(ArrayList<MoviesModel>... values) {
        if (values != null) {
            movieAsyncTaskCompleteListener.onTaskComplete(values[0]);
        }

    }

    @Override
    protected void onCancelled() {
        isCancelled = false;
        new FetchMoviesTask(context , movieAsyncTaskCompleteListener, movieListCategory).execute(movieListCategory);

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
                        Toast.makeText(context, R.string.end_reached, Toast.LENGTH_LONG).show();
                        break;

                    }
                    numberOfApiCalls++;

                } catch (IOException | JSONException ie) {
                    ie.printStackTrace();
                }
            }


        }
        else{
            return  null;
        }
        return mMovieDetailsArrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<MoviesModel> moviesModels) {
        super.onPostExecute(moviesModels);
        if(moviesModels == null ){
            Toast.makeText(context,R.string.no_internet,Toast.LENGTH_LONG).show();
        }

    }

}