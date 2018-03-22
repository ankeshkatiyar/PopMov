package popmov.com.popmov.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import popmov.com.popmov.Utils.MovieConstants;
import popmov.com.popmov.Utils.MoviesHelperClass;
import popmov.com.popmov.Models.MoviesModel;
import popmov.com.popmov.R;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    public interface MovieListCLickListener {

        void onMovieClick(MoviesModel moviesModel);
    }

    public interface MoviePosition {
        void currentPosition(int position, int totalItems);
    }

   public  final MovieListCLickListener mMovieListCLickListener;
    private final static String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185/";
    private int mNumberOfMovies;
    private Context context;
    private final ArrayList<MoviesModel> mMovieDetailsArrayList = new ArrayList<>();
    private MoviePosition moviePosition = null;

    public MoviesAdapter(MovieListCLickListener movieListCLickListener, MoviePosition moviePosition) {
        mNumberOfMovies = MovieConstants.NUMBER_OF_MOVIES;
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

        if (!mMovieDetailsArrayList.isEmpty()) {

            String moviePosterPath = MOVIE_POSTER_BASE_URL + mMovieDetailsArrayList.get(position).getMoviePosterPath();

            if(mMovieDetailsArrayList.get(position).getMoviePosterPath() == null){
                Bitmap bitmap =  MoviesHelperClass.getImage(mMovieDetailsArrayList.get(position).getMovieImage());
                holder.moviePoster.setImageBitmap(bitmap);
            }else {
                Picasso.with(context).load(moviePosterPath).into(holder.moviePoster);
            }
            moviePosition.currentPosition(position, getItemCount());


        }


    }

    public void addItem(ArrayList<MoviesModel> moviesData) {


        mMovieDetailsArrayList.addAll(mMovieDetailsArrayList.size(), moviesData);
        mNumberOfMovies = mMovieDetailsArrayList.size();
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

        private final ImageView moviePoster;

        @Override
        public void onClick(View view) {
            int moviePosition = getAdapterPosition();
            if (!(moviePosition < 0)) {
                try {
                    mMovieListCLickListener.onMovieClick(mMovieDetailsArrayList.get(moviePosition));
                }catch (ArrayIndexOutOfBoundsException aiobe){
                    aiobe.printStackTrace();
                }
            }


        }

        public MoviesViewHolder(View itemView) {
            super(itemView);
            moviePoster = itemView.findViewById(R.id.movies);
            itemView.setOnClickListener(this);


        }


    }


}
