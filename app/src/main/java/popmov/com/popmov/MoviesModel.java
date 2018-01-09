//package popmov.com.popmov;
//
//
//class MoviesModel {
//    private String mMovieName;
//    private String mMovieOverview;
//    private String mMovieRating;
//    private String mMovieReleaseDate;
//    private String mMoviePosterPath;
//
//    MoviesModel(String movieName, String movieOverview, String movieRating, String date, String moviePosterPath) {
//        mMovieName = movieName;
//        mMovieOverview = movieOverview;
//        mMovieRating = movieRating;
//        mMovieReleaseDate = date;
//        mMoviePosterPath = moviePosterPath;
//    }
//
//    String getMovieName() {
//        return mMovieName;
//    }
//
//    String getMovieOverview() {
//        return mMovieOverview;
//    }
//
//    String getMovieRating() {
//        return mMovieRating;
//    }
//
//    String getMovieReleaseDate() {
//        return mMovieReleaseDate;
//    }
//
//    String getMoviePosterPath() {
//        return mMoviePosterPath;
//    }
//
//
//}
package popmov.com.popmov;

import android.os.Parcel;
import android.os.Parcelable;

public class MoviesModel implements Parcelable {

    private String mMovieName;
    private String mMovieOverview;
    private String mMovieRating;
    private String mMovieReleaseDate;
    private String mMoviePosterPath;

    private MoviesModel(Parcel parcel){
        mMovieName = parcel.readString();
        mMovieOverview = parcel.readString();
        mMovieRating = parcel.readString();
        mMovieReleaseDate = parcel.readString();
        mMoviePosterPath = parcel.readString();

    }

    MoviesModel(String movieName, String movieOverview, String movieRating, String date, String moviePosterPath) {
        mMovieName = movieName;
        mMovieOverview = movieOverview;
        mMovieRating = movieRating;
        mMovieReleaseDate = date;
        mMoviePosterPath = moviePosterPath;
    }

    String getMovieName() {
        return mMovieName;
    }

    String getMovieOverview() {
        return mMovieOverview;
    }

    String getMovieRating() {
        return mMovieRating;
    }

    String getMovieReleaseDate() {
        return mMovieReleaseDate;
    }

    String getMoviePosterPath() {
        return mMoviePosterPath;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mMovieName);
        parcel.writeString(mMovieOverview);
        parcel.writeString(mMovieRating);
        parcel.writeString(mMovieReleaseDate);
        parcel.writeString(mMoviePosterPath);

    }

    public  static final Parcelable.Creator<MoviesModel> CREATOR = new Parcelable.Creator<MoviesModel>(){
        @Override
        public MoviesModel createFromParcel(Parcel parcel) {
            return new MoviesModel(parcel);
        }

        @Override
        public MoviesModel[] newArray(int i) {
            return new MoviesModel[i];
        }
    };
}
