package popmov.com.popmov.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import popmov.com.popmov.Adapters.MovieReviewsAdapter;
import popmov.com.popmov.MovieDetails;
import popmov.com.popmov.Models.MovieReviewsModel;
import popmov.com.popmov.Utils.NetworkUtils;


public class FetchMovieReviewsTask extends AsyncTask<String, Void, ArrayList<MovieReviewsModel>>  {

    private final ArrayList<MovieReviewsModel> movieReviews = new ArrayList<>();
    private String movieReviewsJson;
    private final Context context;
    private final MovieDetails.MovieReviewsInterface movieReviewsInterface;
    private ProgressDialog progress ;



    @Override
    protected void onPreExecute() {
        progress = new ProgressDialog(context);
        progress.setTitle("Loading");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    private final RecyclerView recyclerView;
    private MovieReviewsAdapter moviesReviewsAdapter;

    public FetchMovieReviewsTask(Context context, RecyclerView recyclerView , MovieDetails.MovieReviewsInterface movieReviewsInterface) {

        this.recyclerView = recyclerView;
        this.context = context;
        this.movieReviewsInterface = movieReviewsInterface;
    }

    @Override


    protected ArrayList<MovieReviewsModel> doInBackground(String... strings) {
        try {
            movieReviewsJson = NetworkUtils.getResponseFromHttp(NetworkUtils.buildVideoURL(strings[0], "reviews"));

            JSONObject jsonObject = new JSONObject(movieReviewsJson);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject singleMovieReview = jsonArray.getJSONObject(i);
                movieReviews.add(new MovieReviewsModel(singleMovieReview.getString("id"), singleMovieReview.getString("author")
                        , singleMovieReview.getString("content"), singleMovieReview.getString("url")));

            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return movieReviews;
    }


    @Override
    protected void onPostExecute(ArrayList<MovieReviewsModel> strings) {
        moviesReviewsAdapter = new MovieReviewsAdapter(strings);
        movieReviewsInterface.getMovieReviews(strings);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(moviesReviewsAdapter);
        progress.dismiss();
    }


}

