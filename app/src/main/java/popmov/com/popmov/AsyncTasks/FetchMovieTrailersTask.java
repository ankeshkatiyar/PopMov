package popmov.com.popmov.AsyncTasks;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import popmov.com.popmov.Adapters.MoviesTrailerAdapter;
import popmov.com.popmov.MovieDetails;
import popmov.com.popmov.Utils.NetworkUtils;


public  class FetchMovieTrailersTask extends AsyncTask<String, Void, ArrayList<String>> implements DialogInterface.OnCancelListener {
    private final ArrayList<String> movieTrailers = new ArrayList<>();
    private String movieTrailersJson;
    private final Context context;
    private final MovieDetails.MovieTrailerValueInterface movieTrailerValueInterface;
    private final RecyclerView recyclerView;
    private MoviesTrailerAdapter moviesTrailerAdapter;
    private ProgressDialog progress ;

    @Override
    protected void onPreExecute() {
       progress =  new ProgressDialog(context);
        progress.setTitle("Loading");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    private final MoviesTrailerAdapter.MovieClickListener movieClickListener = new MoviesTrailerAdapter.MovieClickListener() {
        @Override
        public void onMovieClick(String trailer) {
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + trailer));
            try {
                context.startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                context.startActivity(webIntent);
            }


        }
    };

    public FetchMovieTrailersTask(Context context, RecyclerView recyclerView, MovieDetails.MovieTrailerValueInterface movieTrailerValueInterface) {

        this.movieTrailerValueInterface = movieTrailerValueInterface;
        this.recyclerView = recyclerView;
        this.context = context;
    }

    @Override


    protected ArrayList<String> doInBackground(String... strings) {
        try {
            movieTrailersJson = NetworkUtils.getResponseFromHttp(NetworkUtils.buildVideoURL(strings[0], "videos"));

            JSONObject jsonObject = new JSONObject(movieTrailersJson);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject singleMovieTrailer = jsonArray.getJSONObject(i);
                movieTrailers.add(singleMovieTrailer.getString("key"));

            }


        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return movieTrailers;
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {

        moviesTrailerAdapter = new MoviesTrailerAdapter(movieClickListener, strings);
        movieTrailerValueInterface.getMovieTrailers(strings);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(moviesTrailerAdapter);
        progress.dismiss();

    }


    @Override
    public void onCancel(DialogInterface dialogInterface) {

    }
}

