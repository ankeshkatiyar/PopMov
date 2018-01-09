package popmov.com.popmov;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    public interface MovieListCLickListener {

        void onMovieClick(MoviesModel moviesModel);
    }

    public interface MoviePosition {
        void currentPosition(int position, int totalItems);
    }

    private final MovieListCLickListener mMovieListCLickListener;
    private final static String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185/";
    private int mNumberOfMovies;
    private Context context;
    private static int pos = 2;
    private ArrayList<MoviesModel> mMovieDetailsArrayList = new ArrayList<>();
    private MoviePosition moviePosition = null;

    MoviesAdapter(int numberOfMovies, MovieListCLickListener movieListCLickListener, MoviePosition moviePosition) {
        mNumberOfMovies = numberOfMovies;
        mMovieListCLickListener = movieListCLickListener;
        this.moviePosition = moviePosition;
    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.movies, parent, false);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesViewHolder holder, int position) {
//        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)holder.moviePoster.getLayoutParams();
//        if(position%2 != 0){
//            params.leftMargin = 16;
//        }
//        else{
//            params.rightMargin = 16;
//        }

        if (!mMovieDetailsArrayList.isEmpty()) {
            String moviePosterPath = MOVIE_POSTER_BASE_URL + mMovieDetailsArrayList.get(position).getMoviePosterPath();
            Picasso.with(context).load(moviePosterPath).into(holder.moviePoster);
            moviePosition.currentPosition(position, getItemCount());


        }


    }

    public void addItem(ArrayList<MoviesModel> moviesData) {
        mNumberOfMovies = mMovieDetailsArrayList.size();
        mMovieDetailsArrayList.addAll(mMovieDetailsArrayList.size(), moviesData);
        notifyDataSetChanged();
    }

    public void removeAllItems() {
        mMovieDetailsArrayList.clear();
    }

    @Override
    public int getItemCount() {
        return mNumberOfMovies;
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView moviePoster;

        @Override
        public void onClick(View view) {
            int moviePosition = getAdapterPosition();
            if (!(moviePosition < 0)) {
                mMovieListCLickListener.onMovieClick(mMovieDetailsArrayList.get(moviePosition));
            }


        }

        public MoviesViewHolder(View itemView) {
            super(itemView);
            moviePoster = itemView.findViewById(R.id.movies);
            itemView.setOnClickListener(this);


        }


    }


}
