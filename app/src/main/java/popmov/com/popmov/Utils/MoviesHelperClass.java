package popmov.com.popmov.Utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import popmov.com.popmov.ContentProviders.MoviesContentProviders;
import popmov.com.popmov.Contracts.FavMovieListContract;
import popmov.com.popmov.FetchMoviesTask;
import popmov.com.popmov.Models.MoviesModel;


public class MoviesHelperClass {




    public ArrayList<MoviesModel> getFavouriteMovies(FetchMoviesTask fetchMoviesTask, ContentResolver contentResolver) {

        ArrayList<MoviesModel> moviesModels = new ArrayList<>();

        //Since we are selecting the fav movies the async task can be cancelled
        if(fetchMoviesTask!=null) {
            fetchMoviesTask.cancel(false);
        }
        Cursor movieData = contentResolver.query(MoviesContentProviders.CONTENT_URI_MOVIE, null,null,null,null);


        if (movieData.moveToFirst()) {
            do {
                moviesModels.add(new MoviesModel(
                        movieData.getString(movieData.getColumnIndex(FavMovieListContract.FavMovieEntry.COULUMN_MOVIE_ID)),
                        movieData.getString(movieData.getColumnIndex(FavMovieListContract.FavMovieEntry.COULUMN_MOVIE_NAME)),
                        movieData.getString(movieData.getColumnIndex(FavMovieListContract.FavMovieEntry.COULUMN_SUMMARY)),
                        movieData.getString(movieData.getColumnIndex(FavMovieListContract.FavMovieEntry.COULUMN_RATING)),
                        movieData.getString(movieData.getColumnIndex(FavMovieListContract.FavMovieEntry.COULUMN_RELEASE_DATE)),
                        movieData.getBlob(movieData.getColumnIndex(FavMovieListContract.FavMovieEntry.COULUMN_MOVIE_IMAGE)))

                );


                Log.i("Movies", movieData.getString(movieData.getColumnIndex(FavMovieListContract.FavMovieEntry.COULUMN_MOVIE_NAME)));


            } while (movieData.moveToNext());
            movieData.close();

        }
        return moviesModels;
    }

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
