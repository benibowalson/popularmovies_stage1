package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private TextView tv_title;
    private ImageView iv_detail;
    private WebView tv_synopsis;
    private TextView tv_release_date;
    private TextView tv_rating;
    Movie clickedMovie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tv_title = (TextView)findViewById(R.id.tv_title);
        iv_detail = (ImageView)findViewById(R.id.iv_detail);
        tv_synopsis = (WebView)findViewById(R.id.tv_synopsis);
        tv_release_date = (TextView)findViewById(R.id.tv_release_date);
        tv_rating = (TextView)findViewById(R.id.tv_rating);

        if(savedInstanceState == null){
            Intent sourceIntent = getIntent();
            Bundle extras = sourceIntent.getExtras();
            if(extras != null){

                clickedMovie = (Movie) extras.getParcelable("Movie_Object");

                try{
                    writeFields();
                } catch (Exception ex){
                    Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            clickedMovie = savedInstanceState.getParcelable("aMovie");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("aMovie", clickedMovie);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        writeFields();
    }

    void loadWebView(String strTextToLoad){
        String myText = "";
        myText += "<html><body style=\"font-size:25\"><p align=\"justify\">";
        myText += strTextToLoad + "</p></body></html>";
        tv_synopsis.loadData(myText, "text/html", "utf-8");
    }

    void writeFields(){
        tv_title.setText(clickedMovie.original_title);
        Picasso.with(this).load(clickedMovie.backdrop_path).into(iv_detail);
        loadWebView(clickedMovie.overview);
        tv_release_date.setText(clickedMovie.release_date);
        tv_rating.setText(Double.toString(clickedMovie.vote_average));
    }
}
