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
package popmov.com.popmov.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class MoviesModel implements Parcelable {

    private final String mMovieName;
    private final String mMovieOverview;
    private final String mMovieRating;
    private final String mMovieReleaseDate;
    private String mMoviePosterPath;
    private final String mMovieId;
    private byte[] mMovieImage;
    private int mIsFavMovie ;

    private MoviesModel(Parcel parcel){
        mMovieName = parcel.readString();
        mMovieOverview = parcel.readString();
        mMovieRating = parcel.readString();
        mMovieReleaseDate = parcel.readString();
        mMoviePosterPath = parcel.readString();
        mMovieId = parcel.readString();
        mIsFavMovie = parcel.readInt();
        mMovieImage = new byte[parcel.readInt()];
        parcel.readByteArray(mMovieImage);



    }

    public MoviesModel(String movieId ,String movieName, String movieOverview, String movieRating, String date, String moviePosterPath) {
        mMovieId = movieId;
        mMovieName = movieName;
        mMovieOverview = movieOverview;
        mMovieRating = movieRating;
        mMovieReleaseDate = date;
        mMoviePosterPath = moviePosterPath;

    }
   public  MoviesModel(String movieId, String movieName, String movieOverview, String movieRating, String date, byte[] movieImage)  {
        mMovieId = movieId;
        mMovieName = movieName;
        mMovieOverview = movieOverview;
        mMovieRating = movieRating;
        mMovieReleaseDate = date;
        mMovieImage = movieImage;
        mIsFavMovie = 1;

    }



    public String getMovieName() {
        return mMovieName;
    }

    public String getMovieOverview() {
        return mMovieOverview;
    }

    public String getMovieRating() {
        return mMovieRating;
    }

    public String getMovieReleaseDate() {
        return mMovieReleaseDate;
    }

    public String getMoviePosterPath() {
        return mMoviePosterPath;
    }

    public String getMovieId() { return  mMovieId; }

    public byte[] getMovieImage() { return  mMovieImage; }

    public int getIsFavMovie(){return  mIsFavMovie;}

    public void setIsFavMovie(){mIsFavMovie = 1;}

    public void setMovieImage(byte[] imageBytes){ mMovieImage = imageBytes;}


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
        parcel.writeString(mMovieId);
        parcel.writeInt(mIsFavMovie);
        if (mMovieImage != null) {
            parcel.writeInt(mMovieImage.length);
            parcel.writeByteArray(mMovieImage);
        }


    }

    public static final Creator<MoviesModel> CREATOR = new Creator<MoviesModel>() {
        @Override
        public MoviesModel createFromParcel(Parcel in) {
            return new MoviesModel(in);
        }

        @Override
        public MoviesModel[] newArray(int size) {
            return new MoviesModel[size];
        }
    };

}
