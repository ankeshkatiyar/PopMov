package popmov.com.popmov;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import popmov.com.popmov.Adapters.MovieReviewsAdapter;
import popmov.com.popmov.Adapters.MoviesTrailerAdapter;
import popmov.com.popmov.AsyncTasks.FetchMovieReviewsTask;
import popmov.com.popmov.AsyncTasks.FetchMovieTrailersTask;
import popmov.com.popmov.ContentProviders.MoviesContentProviders;
import popmov.com.popmov.Contracts.FavMovieListContract;
import popmov.com.popmov.Models.MovieReviewsModel;
import popmov.com.popmov.Utils.MoviesHelperClass;
import popmov.com.popmov.Models.MoviesModel;


public class MovieDetails extends AppCompatActivity {


    public interface MovieTrailerValueInterface {
        void getMovieTrailers(ArrayList<String> movieTrailers);
    }

    public interface MovieReviewsInterface {
        void getMovieReviews(ArrayList<MovieReviewsModel> movieReviews);
    }

    private final static String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185/";
    private ArrayList<String> mMovieTrailers = new ArrayList<>();
    private ArrayList<MovieReviewsModel> mMovieReviews = new ArrayList<>();
    private MoviesModel movieModel;
    private RecyclerView recyclerView;
    private RecyclerView movieReviewsRecyclerView;
    private MoviesTrailerAdapter movieTrailersAdapter;
    private SQLiteDatabase sqLiteDatabase;
    private String moviePosterPath;
    private MovieReviewsAdapter mMovieReviewAdapter;
    private ImageView moviePoster;
    private TextView movieRating;
    private TextView movieOverview;
    private TextView movieReleaseDate;
    private TextView movieTitle;
    private Bundle bundle;
    private boolean isMovieFav = false;
    private ImageView favMovie;
    ContentResolver contentResolver;
    ScrollView scrollView ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        movieOverview = findViewById(R.id.movie_overview);
        movieRating = findViewById(R.id.movie_rating);
        movieReleaseDate = findViewById(R.id.movie_release_date);
        movieTitle = findViewById(R.id.movie_title);
        moviePoster = findViewById(R.id.movie_poster);
        favMovie = findViewById(R.id.movie_fav);
        recyclerView = findViewById(R.id.trailers_recycler_view);
        movieReviewsRecyclerView = findViewById(R.id.reviews_recycle_view);
        scrollView =  (ScrollView) findViewById(R.id.movie_details_sv);
        contentResolver = getContentResolver();

        //Listener for the trailer click
        MoviesTrailerAdapter.MovieClickListener movieClickListener = new MoviesTrailerAdapter.MovieClickListener() {
            @Override
            public void onMovieClick(String trailer) {
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + trailer));
                try {
                    startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(webIntent);
                }


            }
        };
        bundle = getIntent().getExtras();

        if (savedInstanceState != null) {

            setRecyclerViewValues(recyclerView);
            setRecyclerViewValues(movieReviewsRecyclerView);
            int[]  position = savedInstanceState.getIntArray("ScrollPosition");
            scrollView.setScrollY(position[0]);
            mMovieTrailers = savedInstanceState.getStringArrayList("MovieTrailers");
            mMovieReviews = savedInstanceState.getParcelableArrayList("MovieReviews");
            recyclerView.setAdapter(new MoviesTrailerAdapter(movieClickListener, mMovieTrailers));
            movieReviewsRecyclerView.setAdapter(new MovieReviewsAdapter(mMovieReviews));
            setAllViewsWithValues();


        } else {

            try {

                //Setting all the Views with the values
                setAllViewsWithValues();
                if (checkMovieIsFavourite(movieModel.getMovieId())) {
                    isMovieFav = true;
                    favMovie.setImageResource(android.R.drawable.btn_star_big_on);
                } else {
                    favMovie.setImageResource(android.R.drawable.btn_star_big_off);
                }

                if (movieModel.getIsFavMovie() == 1) {
                    getFavMoviesTrailers();
                    getFavouriteMovieReviews();

                } else {

                    new FetchMovieTrailersTask(this, recyclerView, new MovieTrailerValueInterface() {
                        @Override
                        public void getMovieTrailers(ArrayList<String> movieTrailers) {
                            mMovieTrailers = movieTrailers;

                        }
                    }).execute(movieModel.getMovieId());
                    new FetchMovieReviewsTask(this, movieReviewsRecyclerView, new MovieReviewsInterface() {
                        @Override
                        public void getMovieReviews(ArrayList<MovieReviewsModel> movieReviews) {
                            mMovieReviews = movieReviews;

                        }
                    }).execute(movieModel.getMovieId());
                }

            } catch (NullPointerException npe) {
                Toast.makeText(this, "Internal Error 1", Toast.LENGTH_SHORT).show();
                npe.printStackTrace();
            }

        }
        favMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMovieFav) {
                    favMovie.setImageResource(android.R.drawable.btn_star_big_off);
                    removeFavMovies();
                    isMovieFav = false;
                } else {

                    favMovie.setImageResource(android.R.drawable.btn_star_big_on);

                    saveFavouriteMovies();
                    isMovieFav = true;
                }
            }
        });

    }

    private void saveFavouriteMovies() {

        if (movieModel.getIsFavMovie() != 1) {
            ContentValues cv = new ContentValues();
            cv.put(FavMovieListContract.FavMovieEntry.COULUMN_MOVIE_ID, movieModel.getMovieId());
            cv.put(FavMovieListContract.FavMovieEntry.COULUMN_MOVIE_NAME, movieModel.getMovieName());
            cv.put(FavMovieListContract.FavMovieEntry.COULUMN_RATING, movieModel.getMovieRating());
            cv.put(FavMovieListContract.FavMovieEntry.COULUMN_RELEASE_DATE, movieModel.getMovieReleaseDate());
            cv.put(FavMovieListContract.FavMovieEntry.COULUMN_SUMMARY, movieModel.getMovieOverview());
            Bitmap bitmap = MoviesHelperClass.getBitmapFromURL(moviePosterPath);
            cv.put(FavMovieListContract.FavMovieEntry.COULUMN_MOVIE_IMAGE, MoviesHelperClass.getBytes(bitmap));

            contentResolver.insert(MoviesContentProviders.CONTENT_URI_MOVIE,cv);
            cv.clear();

            for (String Trailers : mMovieTrailers) {
                cv.put(FavMovieListContract.FavMovieTrailersEntry.COULUMN_MOVIE_ID, movieModel.getMovieId());
                cv.put(FavMovieListContract.FavMovieTrailersEntry.COULUMN_TRAILERS, Trailers);
                contentResolver.insert(MoviesContentProviders.CONTENT_URI_TRAILER,cv);


            }

            cv.clear();

            for (MovieReviewsModel reviews : mMovieReviews) {
                cv.put(FavMovieListContract.FavMovieReviewsEntry.COULUMN_MOVIE_ID, movieModel.getMovieId());
                cv.put(FavMovieListContract.FavMovieReviewsEntry.COULUMN_AUTHOR, reviews.getAuthor());
                cv.put(FavMovieListContract.FavMovieReviewsEntry.COULUMN_REVIEW, reviews.getContent());
                cv.put(FavMovieListContract.FavMovieReviewsEntry.COULUMN_URL, reviews.getUrl());
                contentResolver.insert(MoviesContentProviders.CONTENT_URI_REVIEW,cv);


            }

            Toast.makeText(this, "Movie is set as Favourite", Toast.LENGTH_LONG).show();
        }
    }

    private void removeFavMovies() {


        String[] movieWhereArg = {movieModel.getMovieId()};

        contentResolver.delete(MoviesContentProviders.CONTENT_URI_MOVIE,FavMovieListContract.FavMovieEntry.COULUMN_MOVIE_ID + " = ? ",movieWhereArg);
        contentResolver.delete(MoviesContentProviders.CONTENT_URI_REVIEW,FavMovieListContract.FavMovieReviewsEntry.COULUMN_MOVIE_ID + " = ? ",movieWhereArg);
        contentResolver.delete(MoviesContentProviders.CONTENT_URI_TRAILER,FavMovieListContract.FavMovieTrailersEntry.COULUMN_MOVIE_ID + " = ? ",movieWhereArg);



//        FavouriteMoviesDBHelper.deleteFromDB(FavMovieListContract.FavMovieEntry.TABLE_NAME, sqLiteDatabase, FavMovieListContract.FavMovieEntry.COULUMN_MOVIE_ID, movieModel.getMovieId());
//        FavouriteMoviesDBHelper.deleteFromDB(FavMovieListContract.FavMovieReviewsEntry.TABLE_NAME, sqLiteDatabase, FavMovieListContract.FavMovieReviewsEntry.COULUMN_MOVIE_ID, movieModel.getMovieId());
//        FavouriteMoviesDBHelper.deleteFromDB(FavMovieListContract.FavMovieTrailersEntry.TABLE_NAME, sqLiteDatabase, FavMovieListContract.FavMovieTrailersEntry.COULUMN_MOVIE_ID, movieModel.getMovieId());
        Toast.makeText(this, "Movie as Favourite Removed", Toast.LENGTH_LONG).show();


    }

    private void getFavMoviesTrailers() {

        MoviesTrailerAdapter.MovieClickListener movieClickListener = new MoviesTrailerAdapter.MovieClickListener() {
            @Override
            public void onMovieClick(String trailer) {
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + trailer));
                try {
                    startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(webIntent);
                }


            }
        };

        String tableColums[] = {FavMovieListContract.FavMovieTrailersEntry.COULUMN_TRAILERS};
        String whereClause = FavMovieListContract.FavMovieTrailersEntry.COULUMN_MOVIE_ID + " = ? ";

        String whereArguments[] = {movieModel.getMovieId()};
        Cursor movieData = contentResolver.query(MoviesContentProviders.CONTENT_URI_TRAILER, tableColums,whereClause,whereArguments,null);
        //Cursor movieData = sqLiteDatabase.query(FavMovieListContract.FavMovieTrailersEntry.TABLE_NAME, tableColums, whereClause, whereArguments, null, null, null);
        if (movieData.moveToFirst()) {
            do {

                mMovieTrailers.add(movieData.getString(movieData.getColumnIndex(FavMovieListContract.FavMovieTrailersEntry.COULUMN_TRAILERS)));


            } while (movieData.moveToNext());
            movieData.close();
            movieTrailersAdapter = new MoviesTrailerAdapter(movieClickListener, mMovieTrailers);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(movieTrailersAdapter);

        }


    }

    private void getFavouriteMovieReviews() {

        String tableColums[] = {FavMovieListContract.FavMovieReviewsEntry.COULUMN_AUTHOR,
                FavMovieListContract.FavMovieReviewsEntry.COULUMN_REVIEW,
                FavMovieListContract.FavMovieReviewsEntry.COULUMN_URL,
                FavMovieListContract.FavMovieReviewsEntry.COULUMN_ID};
        String whereClause = FavMovieListContract.FavMovieReviewsEntry.COULUMN_MOVIE_ID + " = ? ";

        String whereArguments[] = {movieModel.getMovieId()};

        Cursor movieData = contentResolver.query(MoviesContentProviders.CONTENT_URI_REVIEW, tableColums,whereClause,whereArguments,null);
        if (movieData.moveToFirst()) {
            do {

                mMovieReviews.add(new MovieReviewsModel(
                        movieData.getString(movieData.getColumnIndex(FavMovieListContract.FavMovieReviewsEntry.COULUMN_ID)),
                        movieData.getString(movieData.getColumnIndex(FavMovieListContract.FavMovieReviewsEntry.COULUMN_AUTHOR)),
                        movieData.getString(movieData.getColumnIndex(FavMovieListContract.FavMovieReviewsEntry.COULUMN_REVIEW)),
                        movieData.getString(movieData.getColumnIndex(FavMovieListContract.FavMovieReviewsEntry.COULUMN_URL))

                ));


            } while (movieData.moveToNext());
            movieData.close();
            mMovieReviewAdapter = new MovieReviewsAdapter(mMovieReviews);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
            movieReviewsRecyclerView.setLayoutManager(gridLayoutManager);
            movieReviewsRecyclerView.setHasFixedSize(true);
            movieReviewsRecyclerView.setAdapter(mMovieReviewAdapter);


        }

    }

    private void setAllViewsWithValues() {
        movieModel = bundle.getParcelable("movie_details");
        movieOverview.setText(movieModel.getMovieOverview());
        movieRating.setText(movieModel.getMovieRating());
        movieReleaseDate.setText(movieModel.getMovieReleaseDate());
        movieTitle.setText(movieModel.getMovieName());
        if (movieModel.getMoviePosterPath() != null) {
            moviePosterPath = MOVIE_POSTER_BASE_URL + movieModel.getMoviePosterPath();
            Picasso.with(this).load(moviePosterPath).into(moviePoster);
        } else {
            Bitmap bitmap = MoviesHelperClass.getImage(movieModel.getMovieImage());
            moviePoster.setImageBitmap(bitmap);
        }
    }

    private boolean checkMovieIsFavourite(String movieId) {

        String tableColums[] = {FavMovieListContract.FavMovieEntry.COULUMN_MOVIE_ID};
        String whereClause = FavMovieListContract.FavMovieEntry.COULUMN_MOVIE_ID + " = ? ";

        String whereArguments[] = {movieModel.getMovieId()};
        Cursor movieData = contentResolver.query(MoviesContentProviders.CONTENT_URI_TRAILER, tableColums, whereClause, whereArguments,null,null);
        return movieData.moveToFirst();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putIntArray("ScrollPosition", new int[]{scrollView.getScrollY()});
        outState.putStringArrayList("MovieTrailers", mMovieTrailers);
        outState.putParcelableArrayList("MovieReviews",mMovieReviews);

    }

    public void setRecyclerViewValues(RecyclerView recyclerView) {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
    }




}




