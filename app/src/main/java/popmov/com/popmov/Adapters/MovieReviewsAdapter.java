package popmov.com.popmov.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import popmov.com.popmov.Models.MovieReviewsModel;
import popmov.com.popmov.R;

/**
 * Created by ankeshkatiyar on 24/02/18.
 */

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewsViewHolder>{


    private ArrayList<MovieReviewsModel> movieReviewsModels = new ArrayList<>();
    private Context context;
    private final int numberOfReviews;

    public MovieReviewsAdapter(ArrayList<MovieReviewsModel> movieReviewsModels) {
        this.movieReviewsModels = movieReviewsModels;
        numberOfReviews = movieReviewsModels.size();

    }

    @Override
    public MovieReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.movies_reviews,parent,false);
        return new MovieReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewsViewHolder holder, int position) {

        holder.author.setText(movieReviewsModels.get(position).getAuthor());
        holder.content.setText(movieReviewsModels.get(position).getContent());

    }

    @Override
    public int getItemCount() {
        return numberOfReviews;
    }

    public  class MovieReviewsViewHolder extends RecyclerView.ViewHolder{
        private final TextView author;
        private final TextView content;
        public MovieReviewsViewHolder(View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.author);
            content = itemView.findViewById(R.id.content);
        }
    }
}
