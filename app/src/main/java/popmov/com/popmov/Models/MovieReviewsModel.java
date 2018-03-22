package popmov.com.popmov.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ankeshkatiyar on 24/02/18.
 */

 public class MovieReviewsModel implements Parcelable {
    private String id;
    private String author;
    private String content;
    private String url;

    public MovieReviewsModel(String id, String author, String content, String url) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
    }

    protected MovieReviewsModel(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
        url = in.readString();
    }

    public static final Creator<MovieReviewsModel> CREATOR = new Creator<MovieReviewsModel>() {
        @Override
        public MovieReviewsModel createFromParcel(Parcel in) {
            return new MovieReviewsModel(in);
        }

        @Override
        public MovieReviewsModel[] newArray(int size) {
            return new MovieReviewsModel[size];
        }
    };

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }


    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(author);
        parcel.writeString(content);
        parcel.writeString(url);
    }
}
