package popmov.com.popmov;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import popmov.com.popmov.Models.MoviesModel;

import java.util.ArrayList;

import popmov.com.popmov.Adapters.MoviesAdapter;
import popmov.com.popmov.Utils.MovieConstants;
import popmov.com.popmov.Utils.MoviesHelperClass;



//This interface is to get the results in the activity from the AsyncTask
interface MoviesAsyncTaskCompleteListener<T> {
    void onTaskComplete(T result);

    void onRemoveItems();

}

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieListCLickListener {


    private MoviesAdapter moviesAdapter;
    private String movieListCategory = MovieConstants.POPULAR_MOVIES;
    private Context context;
    private SQLiteDatabase sqLiteDatabase;
    private ArrayList<MoviesModel> moviesModels = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private FetchMoviesTask fetchMoviesTask;
    private MoviesHelperClass moviesHelperClass;
    RecyclerView moviesRecyclerView;
    private int spanCount;
    private ContentResolver contentResolver ;
    //Getting the results from the async task for the movies and then adding it to the adapter
    public class FetchMyDataTaskCompleteListener implements MoviesAsyncTaskCompleteListener<ArrayList<MoviesModel>> {


        @Override
        public void onTaskComplete(ArrayList<MoviesModel> result) {
            moviesAdapter.addItem(result);
            moviesModels.addAll(result);
        }

        @Override
        public void onRemoveItems() {
            if (moviesAdapter.getItemCount() != 0) {
                moviesAdapter.removeAllItems();

            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        moviesHelperClass = new MoviesHelperClass();
        setContentView(R.layout.activity_main);
        moviesRecyclerView = findViewById(R.id.rv_top_movies);
        contentResolver =getContentResolver();

        readPreferences();

        int ot = getResources().getConfiguration().orientation;
        if(ot == Configuration.ORIENTATION_LANDSCAPE){
           spanCount = 4;
        }
        else {
            spanCount = 2;
        }
        moviesAdapter = new MoviesAdapter(this, new MoviesAdapter.MoviePosition() {

                /*This method is there to call asynctask again when user is about to reach the end of
                   the scrolling
               */
                @Override
                public void currentPosition(int position, int totalItems) {

                    if (position == totalItems - 50) {

                        fetchMoviesTask = new FetchMoviesTask(context, new FetchMyDataTaskCompleteListener(), movieListCategory);
                        fetchMoviesTask.cancel(true);

                    }
                }
            });

        gridLayoutManager = new GridLayoutManager(getApplicationContext(), spanCount);
        if(savedInstanceState!=null){
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable("RecyclerList");
            moviesModels = savedInstanceState.getParcelableArrayList("ArrayList");
            gridLayoutManager.onRestoreInstanceState(savedRecyclerLayoutState);


            moviesAdapter.addItem(moviesModels);

        }
        moviesRecyclerView.setLayoutManager(gridLayoutManager);
        moviesRecyclerView.setHasFixedSize(true);
        moviesRecyclerView.setAdapter(moviesAdapter);
        if(savedInstanceState==null) {
            if(movieListCategory.equals(MovieConstants.FAVOURITE_MOVIES)){
                moviesModels = moviesHelperClass.getFavouriteMovies(fetchMoviesTask,contentResolver);
                moviesAdapter.addItem(moviesModels);
            }else {
                fetchMoviesTask = new FetchMoviesTask(context, new FetchMyDataTaskCompleteListener(), movieListCategory);
                fetchMoviesTask.execute(movieListCategory);
            }
        }


    }


    //Method to get  details of a movie on a click
    @Override
    public void onMovieClick(MoviesModel moviesModel) {

        /*Here we are checking that if we are selecting the favourite movies then we should pass the image got from the
        database to the movie details activity*/
        if (movieListCategory.equals(MovieConstants.FAVOURITE_MOVIES)) {
            moviesModel.setIsFavMovie();
            int index = moviesModels.indexOf(moviesModel);
            moviesModel.setMovieImage(moviesModels.get(index).getMovieImage());
        }
        Intent intent = new Intent(this, MovieDetails.class);
        intent.putExtra(MovieConstants.MOVIE_DETAILS, moviesModel);
        startActivity(intent);

    }

    //Resetting the current page number to 1 whenever any other movielist category is selected to get the results again.
    @Override
    protected void onStop() {
        super.onStop();
        FetchMoviesTask.currentPageNumber = 1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sort, menu);
        return true;
    }

    //Getting the movies based on the user preference
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.top_movies:
                if (!movieListCategory.equals(MovieConstants.TOP_RATED_MOVIES)) {
                    FetchMoviesTask.isCancelled = true;
                    movieListCategory = MovieConstants.TOP_RATED_MOVIES;
                    /*If the ASYNC task is running then it will be cancelled and will be called within
                    OnCancelled method.But if it is not running then it has to be called from here
                    */
                    moviesAdapter.removeAllItems();

                    if (new FetchMoviesTask(context, new FetchMyDataTaskCompleteListener(), movieListCategory).getStatus() != AsyncTask.Status.RUNNING) {
                        fetchMoviesTask = new FetchMoviesTask(context, new FetchMyDataTaskCompleteListener(), movieListCategory);
                        fetchMoviesTask.execute(movieListCategory);
                    }
                    createPreferences();

                }
                break;

            case R.id.popular_movies:
                if (!movieListCategory.equals(MovieConstants.POPULAR_MOVIES)) {
                    FetchMoviesTask.isCancelled = true;
                    movieListCategory = MovieConstants.POPULAR_MOVIES;
                    moviesAdapter.removeAllItems();
                    if (new FetchMoviesTask(context, new FetchMyDataTaskCompleteListener(), movieListCategory).getStatus() != AsyncTask.Status.RUNNING) {
                        fetchMoviesTask = new FetchMoviesTask(context, new FetchMyDataTaskCompleteListener(), movieListCategory);
                        fetchMoviesTask.execute(movieListCategory);
                    }
                    createPreferences();
                }
                break;
            default:
                break;
            case R.id.fav_movies:

                movieListCategory = MovieConstants.FAVOURITE_MOVIES;
                moviesAdapter.removeAllItems();
                moviesModels = moviesHelperClass.getFavouriteMovies(fetchMoviesTask, contentResolver);
                moviesAdapter.addItem(moviesModels);
                createPreferences();

                break;

        }
        return true;

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("RecyclerList",gridLayoutManager.onSaveInstanceState());
        outState.putParcelableArrayList("ArrayList",moviesModels);


    }

     public void createPreferences(){

         SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
         SharedPreferences.Editor editor =  sharedPreferences.edit();
         editor.putString(getString((R.string.movie_category)), movieListCategory);
         editor.commit();
     }

     public void readPreferences(){
         SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
         String defaultValue = MovieConstants.POPULAR_MOVIES;
         movieListCategory = sharedPref.getString(getString(R.string.movie_category), defaultValue);
     }


}