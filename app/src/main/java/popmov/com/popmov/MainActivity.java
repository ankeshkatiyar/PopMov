package popmov.com.popmov;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

interface MoviesAsyncTaskCompleteListener<T> {
    void onTaskComplete(T result);
    void onRemoveItems();

}

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieListCLickListener {



    private static final String MOVIE_DETAILS = "movie_details";
    private MoviesAdapter moviesAdapter;
    private static final int NUMBER_OF_MOVIES = 5;
    private final static String POPULAR_MOVIES = "popular";
    private final static String TOP_RATED_MOVIES = "top_rated";
    private String movieListCategory = POPULAR_MOVIES;
    private Context context;

    public class FetchMyDataTaskCompleteListener implements MoviesAsyncTaskCompleteListener<ArrayList<MoviesModel>> {

        @Override
        public void onTaskComplete(ArrayList<MoviesModel> result) {
            moviesAdapter.addItem(result);
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
        setContentView(R.layout.activity_main);
        RecyclerView moviesRecyclerView;
        context = getApplicationContext();
        moviesRecyclerView = findViewById(R.id.rv_top_movies);
        moviesAdapter = new MoviesAdapter(NUMBER_OF_MOVIES, this, new MoviesAdapter.MoviePosition() {

            /*This method is there to call asynctask again when user is about to reach the end of
               the scrolling
           */
            @Override
            public void currentPosition(int position, int totalItems) {

                if (position == totalItems - 20) {

                    new FetchMoviesTask(context, new FetchMyDataTaskCompleteListener(), movieListCategory).execute(movieListCategory);

                }
            }
        });
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
            moviesRecyclerView.setLayoutManager(gridLayoutManager);
            moviesRecyclerView.setHasFixedSize(true);
            moviesRecyclerView.setAdapter(moviesAdapter);
            new FetchMoviesTask(context, new FetchMyDataTaskCompleteListener(), movieListCategory).execute(POPULAR_MOVIES);


    }


    //Method to get  details of a movie on a click
    @Override
    public void onMovieClick(MoviesModel moviesModel) {
        Intent intent = new Intent(this, MovieDetails.class);
        intent.putExtra(MOVIE_DETAILS, moviesModel);
        startActivity(intent);

    }

    //Resetting the current page number to 1 to get the results again.
    @Override
    protected void onStop() {
        super.onStop();
        FetchMoviesTask.currentPageNumber = 1 ;
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
                    FetchMoviesTask.isCancelled = true;
                    movieListCategory = TOP_RATED_MOVIES;
                    /*If the ASYNC task is running then it will be cancelled and will be called within
                    OnCancelled method.But if it is not running then it has to be called from here
                    */

                    if (new FetchMoviesTask(context, new FetchMyDataTaskCompleteListener(), movieListCategory).getStatus() != AsyncTask.Status.RUNNING) {
                        new FetchMoviesTask(context, new FetchMyDataTaskCompleteListener(), movieListCategory).execute(movieListCategory);
                    }

                }
                break;

            case R.id.popular_movies:
                if (!movieListCategory.equals(POPULAR_MOVIES)) {
                    FetchMoviesTask.isCancelled = true;
                    movieListCategory = POPULAR_MOVIES;
                    if (new FetchMoviesTask(context, new FetchMyDataTaskCompleteListener(), movieListCategory).getStatus() != AsyncTask.Status.RUNNING) {
                        new FetchMoviesTask(context, new FetchMyDataTaskCompleteListener(), movieListCategory).execute(movieListCategory);
                    }
                }
                break;
            default:
                break;
        }
        return true;

    }





}