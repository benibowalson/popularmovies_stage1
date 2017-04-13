package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 48101040 on 4/7/2017.
 */

public class Movie implements Parcelable {

     String poster_path;
    String overview;
     boolean adult;
     String release_date;
     List<Integer> genre_ids;
     int id;
     String original_title;
     String original_language;
     String title;
     String backdrop_path;
     double popularity;
     int vote_count;
     boolean video;
     double vote_average;

    public Movie(String poster_path, String overview, boolean adult, String release_date, List<Integer> genre_ids, int id, String original_title, String original_language, String title, String backdrop_path, double popularity, int vote_count, boolean video, double vote_average) {
        this.poster_path = poster_path;
        this.overview = overview;
        this.adult = adult;
        this.release_date = release_date;
        this.genre_ids = genre_ids;
        this.id = id;
        this.original_title = original_title;
        this.original_language = original_language;
        this.title = title;
        this.backdrop_path = backdrop_path;
        this.popularity = popularity;
        this.vote_count = vote_count;
        this.video = video;
        this.vote_average = vote_average;
    }

    private Movie(Parcel in){
        poster_path = in.readString();
        overview = in.readString();
        adult = (in.readInt() == 0)? false: true;
        release_date = in.readString();
        genre_ids = new ArrayList<Integer>();
        in.readList(genre_ids, null);               //doubting
        id = in.readInt();
        original_title = in.readString();
        original_language = in.readString();
        title = in.readString();
        backdrop_path = in.readString();
        popularity = in.readDouble();
        vote_count = in.readInt();
        video = (in.readInt() == 0)? false: true;
        vote_average = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(poster_path);
        dest.writeString(overview);
        dest.writeInt(adult? 1: 0);
        dest.writeString(release_date);
        dest.writeList(genre_ids);
        dest.writeInt(id);
        dest.writeString(original_title);
        dest.writeString(original_language);
        dest.writeString(title);
        dest.writeString(backdrop_path);
        dest.writeDouble(popularity);
        dest.writeInt(vote_count);
        dest.writeInt(video? 1: 0);
        dest.writeDouble(vote_average);
    }

    public String toString(){
        return title + ", " + release_date;
    }

    public final static Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int i){
            return new Movie[i];
        }
    };
}
