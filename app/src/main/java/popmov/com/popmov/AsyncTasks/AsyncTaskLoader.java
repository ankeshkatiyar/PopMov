//package popmov.com.popmov.AsyncTasks;
//
//import android.annotation.SuppressLint;
//import android.app.LoaderManager;
//import android.content.Context;
//import android.content.Loader;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//
//import popmov.com.popmov.MainActivity;
//import popmov.com.popmov.MoviesAsyncTaskCompleteListener;
//import popmov.com.popmov.Utils.MoviesJsonUtils;
//import popmov.com.popmov.Models.MoviesModel;
//import popmov.com.popmov.Utils.NetworkUtils;
//import popmov.com.popmov.R;
//
//import static android.content.Context.CONNECTIVITY_SERVICE;
//
//
//class  AsyncTaskLoader implements LoaderManager.LoaderCallbacks<ArrayList<MoviesModel>>{
//
//    static WeakReference<MainActivity> mActivity;
//    private static int mTotalPages = -1;
//    private static int currentPageNumber = 1;
//    private Toast toast;
//    private final static String POPULAR_MOVIES = "popular";
//    private final static String TOP_RATED_MOVIES = "top_rated";
//    private final static String TOTAL_PAGES = "total_pages";
//    private String movieListCategory = POPULAR_MOVIES;
//    private static String currentListCategory = POPULAR_MOVIES;
//    private static final boolean isCancelled = false;
//    private int numberOfApiCalls = 1;
//    private Context context = null;
//    private MoviesAsyncTaskCompleteListener movieAsyncTaskCompleteListener;
//    private ArrayList<MoviesModel> mMovieDetailsArrayList = new ArrayList<>();
//    private final String moviesListCategory;
//
//    public AsyncTaskLoader(Context context , String moviesListCategory){
//        this.context = context;
//        this.moviesListCategory = moviesListCategory;
//
//    }
//
//    private boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//    }
//
//    @SuppressLint("StaticFieldLeak")
//    @Override
//    public Loader<ArrayList<MoviesModel>> onCreateLoader(int i, Bundle bundle) {
//
//        return new android.content.AsyncTaskLoader<ArrayList<MoviesModel>>(context) {
//            @Override
//            public ArrayList<MoviesModel> loadInBackground() {
//                numberOfApiCalls = 1;
//                Log.i("Test"," I am here");
//                currentListCategory = moviesListCategory;
//                if (isNetworkAvailable()) {
//                    //Calling only 5 pages at a time to reduce the number of API calls
//                    while (numberOfApiCalls <= 5) {
//
//
//                        if (isCancelled) {
//
//                            cancelLoad();
//                            return null;
//                        }
//                        String movieData;
//
//                        try {
//
//
//                            movieData = NetworkUtils.getResponseFromHttp(NetworkUtils.buildURL(currentPageNumber, currentListCategory));
//                            mMovieDetailsArrayList = MoviesJsonUtils.getMoviesDataFromJSON(movieData);
//                            if (mTotalPages == -1) {
//                                JSONObject jsonObject = new JSONObject(movieData);
//                                mTotalPages = jsonObject.getInt(TOTAL_PAGES);
//                            }
//                            if(mActivity.get() !=null){
//                                mActivity.get().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        publishProgress(mMovieDetailsArrayList);
//                                    }
//                                });
//                            }
//
//                            currentPageNumber++;
//                            if (currentPageNumber > mTotalPages) {
//                                Toast.makeText(context, R.string.end_reached, Toast.LENGTH_LONG).show();
//                                break;
//
//                            }
//                            numberOfApiCalls++;
//
//                        } catch (IOException | JSONException ie) {
//                            ie.printStackTrace();
//                        }
//                    }
//
//
//                }
//                else{
//                    return  null;
//                }
//                Log.i("Test", Integer.toString(mMovieDetailsArrayList.size()));
//                return mMovieDetailsArrayList;
//
//            }
//
//            public void publishProgress(ArrayList<MoviesModel> values){
//                if (values != null) {
//                    movieAsyncTaskCompleteListener.onTaskComplete(values);
//                }
//            }
//
//
//        };
//    }
//
//    @Override
//    public void onLoadFinished(Loader<ArrayList<MoviesModel>> loader, ArrayList<MoviesModel> moviesModels) {
//
//    }
//
//    @Override
//    public void onLoaderReset(Loader<ArrayList<MoviesModel>> loader) {
//
//    }
//}
