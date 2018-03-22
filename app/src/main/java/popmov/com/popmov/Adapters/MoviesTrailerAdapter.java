package popmov.com.popmov.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import popmov.com.popmov.R;


public class MoviesTrailerAdapter extends RecyclerView.Adapter<MoviesTrailerAdapter.MoviesTrailerViewHolder> {
    private final int numberOfMoviesTrailer;
    private final MovieClickListener movieClickListener;
    private Context context;
    private ArrayList<String> mMovieTrailers = new ArrayList<>();

    public interface  MovieClickListener{
        void onMovieClick(String trailer);
    }

    public MoviesTrailerAdapter(MovieClickListener movieClickListener , ArrayList<String> movieTrailers) {
        this.movieClickListener = movieClickListener;
        this.mMovieTrailers = movieTrailers;
        numberOfMoviesTrailer = mMovieTrailers.size();
    }



    @Override
    public MoviesTrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.trailers_layout,parent,false);
        return new MoviesTrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesTrailerViewHolder holder, int position) {
        holder.movieTrailer.setText("Trailer " +Integer.toString(position + 1) );

    }


    @Override
    public int getItemCount() {
        return numberOfMoviesTrailer;
    }

    public class MoviesTrailerViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

        private final TextView movieTrailer;

        public MoviesTrailerViewHolder(View itemView) {
            super(itemView);
            movieTrailer = itemView.findViewById(R.id.trailer_text);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if(!(position < 0)){
                movieClickListener.onMovieClick(mMovieTrailers.get(position));


            }
        }
    }
}
