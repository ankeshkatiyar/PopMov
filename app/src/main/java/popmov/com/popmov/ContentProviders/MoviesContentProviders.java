package popmov.com.popmov.ContentProviders;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import popmov.com.popmov.Contracts.FavMovieListContract;


public class MoviesContentProviders extends ContentProvider {

    static final String PROVIDER_NAME = "popmov.com.popmov.MoviesContentProvider";
    static final String URL_MOVIE = "content://" + PROVIDER_NAME + "/"+ FavMovieListContract.FavMovieEntry.TABLE_NAME + "";
    static final String URL_REVIEW = "content://" + PROVIDER_NAME + "/"+ FavMovieListContract.FavMovieReviewsEntry.TABLE_NAME + "";
    static final String URL_TRAILERS = "content://" + PROVIDER_NAME + "/"+ FavMovieListContract.FavMovieTrailersEntry.TABLE_NAME + "";
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final Uri CONTENT_URI_MOVIE = Uri.parse(URL_MOVIE);
    public static final Uri CONTENT_URI_REVIEW = Uri.parse(URL_REVIEW);
    public static final Uri CONTENT_URI_TRAILER = Uri.parse(URL_TRAILERS);
    private SQLiteDatabase sqLiteDatabase;

    static {
        sUriMatcher.addURI(PROVIDER_NAME, FavMovieListContract.FavMovieEntry.TABLE_NAME, 1);
        sUriMatcher.addURI(PROVIDER_NAME, FavMovieListContract.FavMovieTrailersEntry.TABLE_NAME, 2);
        sUriMatcher.addURI(PROVIDER_NAME, FavMovieListContract.FavMovieReviewsEntry.TABLE_NAME, 3);

    }


    @Override
    public boolean onCreate() {

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        sqLiteDatabase = new FavouriteMoviesDBHelper(getContext()).getReadableDatabase();
        Cursor cursor = null   ;

        switch (sUriMatcher.match(uri)) {
            case 1:
                cursor = sqLiteDatabase.query(FavMovieListContract.FavMovieEntry.TABLE_NAME , strings, s, strings1,null,null,null);
                break;

            case 2:
                cursor =  sqLiteDatabase.query(FavMovieListContract.FavMovieTrailersEntry.TABLE_NAME , strings, s, strings1,null,null,null);
                break;
            case 3:
                cursor =  sqLiteDatabase.query(FavMovieListContract.FavMovieReviewsEntry.TABLE_NAME , strings, s, strings1,null,null,null);
                break;



        }
        return cursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        sqLiteDatabase = new FavouriteMoviesDBHelper(getContext()).getWritableDatabase();
        try {

            switch (sUriMatcher.match(uri)) {
                case 1:
                    sqLiteDatabase.insert(FavMovieListContract.FavMovieEntry.TABLE_NAME, null, contentValues);
                    break;

                case 2:
                    sqLiteDatabase.insert(FavMovieListContract.FavMovieTrailersEntry.TABLE_NAME, null, contentValues);
                    break;
                case 3:
                    sqLiteDatabase.insert(FavMovieListContract.FavMovieReviewsEntry.TABLE_NAME, null, contentValues);
                    break;

            }
            return  uri;
        }catch (SQLException se){
            se.printStackTrace();
            return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {


        sqLiteDatabase = new FavouriteMoviesDBHelper(getContext()).getWritableDatabase();
        try {

            switch (sUriMatcher.match(uri)) {
                case 1:
                    sqLiteDatabase.delete(FavMovieListContract.FavMovieEntry.TABLE_NAME, s,strings);

                case 2:
                    sqLiteDatabase.delete(FavMovieListContract.FavMovieTrailersEntry.TABLE_NAME, s,strings);
                    break;
                case 3:
                    sqLiteDatabase.delete(FavMovieListContract.FavMovieReviewsEntry.TABLE_NAME, s,strings);
                    break;

            }
            return  1;
        }catch (SQLException se){
            se.printStackTrace();
            return 0;
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}

class FavouriteMoviesDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies9.db";
    private static final int DATABASE_VERSION = 1;

    public FavouriteMoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i("Database", "I M Created Successfully");
        final String SQL_CREATE_FAV_MOVIES_LIST = "CREATE TABLE IF NOT EXISTS "
                + FavMovieListContract.FavMovieEntry.TABLE_NAME + "("
                + FavMovieListContract.FavMovieEntry.COULUMN_MOVIE_ID + " INTEGER PRIMARY KEY, "
                + FavMovieListContract.FavMovieEntry.COULUMN_MOVIE_NAME + " TEXT NOT NULL ,"
                + FavMovieListContract.FavMovieEntry.COULUMN_RATING + " INTEGER NOT NULL, "
                + FavMovieListContract.FavMovieEntry.COULUMN_SUMMARY + " TEXT NOT NULL, "
                + FavMovieListContract.FavMovieEntry.COULUMN_RELEASE_DATE + " TEXT NOT NULL, "
                + FavMovieListContract.FavMovieEntry.COULUMN_MOVIE_IMAGE + " BLOB NOT NULL"
                + ");";
        final String SQL_CREATE_FAV_MOVIE_TRAILER_LIST = "CREATE TABLE IF NOT EXISTS "
                + FavMovieListContract.FavMovieTrailersEntry.TABLE_NAME + "("
                + FavMovieListContract.FavMovieTrailersEntry.COULUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FavMovieListContract.FavMovieTrailersEntry.COULUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + FavMovieListContract.FavMovieTrailersEntry.COULUMN_TRAILERS + " TEXT NOT NULL "
                + ");";
        final String SQL_CREATE_FAV_MOVIE_REVIEWS_LIST = "CREATE TABLE IF NOT EXISTS "
                + FavMovieListContract.FavMovieReviewsEntry.TABLE_NAME + "("
                + FavMovieListContract.FavMovieReviewsEntry.COULUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + FavMovieListContract.FavMovieReviewsEntry.COULUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + FavMovieListContract.FavMovieReviewsEntry.COULUMN_AUTHOR + " TEXT NOT NULL, "
                + FavMovieListContract.FavMovieReviewsEntry.COULUMN_REVIEW + " TEXT NOT NULL, "
                + FavMovieListContract.FavMovieReviewsEntry.COULUMN_URL + " TEXT NOT NULL "
                + ");";

        try {
            sqLiteDatabase.execSQL(SQL_CREATE_FAV_MOVIES_LIST);
            sqLiteDatabase.execSQL(SQL_CREATE_FAV_MOVIE_TRAILER_LIST);
            sqLiteDatabase.execSQL(SQL_CREATE_FAV_MOVIE_REVIEWS_LIST);
        } catch (SQLiteException se) {
            Log.i("Database", "Error occured");
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavMovieListContract.FavMovieEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavMovieListContract.FavMovieTrailersEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavMovieListContract.FavMovieReviewsEntry.TABLE_NAME);
            onCreate(sqLiteDatabase);
        } catch (SQLiteException se) {
            se.printStackTrace();

        }


    }





}
