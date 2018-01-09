package popmov.com.popmov;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class MovieDetails extends AppCompatActivity {

    private final static String MOVIE_TITLE = "name";
    private final static String MOVIE_OVERVIEW = "overview";
    private final static String MOVIE_RATING = "rating";
    private final static String MOVIE_RELEASE_DATE = "date";
    private final static String MOVIE_POSTER_URL = "url";
    private final static String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ImageView moviePoster;
        TextView movieRating;
        TextView movieOverview;
        TextView movieReleaseDate;
        TextView movieTitle;
        movieOverview = findViewById(R.id.movie_overview);
        movieRating = findViewById(R.id.movie_rating);
        movieReleaseDate = findViewById(R.id.movie_release_date);
        movieTitle = findViewById(R.id.movie_title);
        moviePoster = findViewById(R.id.movie_poster);
        Bundle bundle = getIntent().getExtras();
        MoviesModel movieModel = bundle.getParcelable("movie_details");
        try {
            movieOverview.setText(movieModel.getMovieOverview());
            movieRating.setText(movieModel.getMovieRating());
            movieReleaseDate.setText(movieModel.getMovieReleaseDate());
            movieTitle.setText(movieModel.getMovieName());
            String moviePosterPath = MOVIE_POSTER_BASE_URL + movieModel.getMoviePosterPath();
            Picasso.with(this).load(moviePosterPath).into(moviePoster);
            RelativeLayout layout = findViewById(R.id.movie_details);
        }catch (NullPointerException npe){
            Toast.makeText(this,"Internal Error",Toast.LENGTH_SHORT).show();
        }


    }

}
