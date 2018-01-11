package popmov.com.popmov;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

class NetworkUtils {
    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String API_KEY = "2ed99c645c2e69c69e8c44ee39de1ad4";
    private static final String QUERY_PARAM_API = "api_key";
    private static final String QUERY_PARAM_PAGE = "page";


    public static URL buildURL(int pageNumber, String movieCategory) {

        URL urlBuild = null;
        Uri uriBuilder = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(movieCategory)
                .appendQueryParameter(QUERY_PARAM_API, API_KEY)
                .appendQueryParameter(QUERY_PARAM_PAGE, Integer.toString(pageNumber))
                .build();


        try {


            return new URL(uriBuilder.toString());
        } catch (MalformedURLException mfe) {
            mfe.printStackTrace();
            return null;

        }


    }

    static String getResponseFromHttp(URL url) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = httpURLConnection.getInputStream();
            Scanner sc = new Scanner(inputStream);
            sc.useDelimiter("//A");
            if (sc.hasNext()) {
                return sc.next();
            } else {

                return null;

            }
        } finally {
            httpURLConnection.disconnect();
        }


    }
}
