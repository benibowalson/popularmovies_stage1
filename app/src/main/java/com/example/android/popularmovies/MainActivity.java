package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements MovieAdapter.IListenToClicks {

    private RecyclerView myRecycler;
    private GridLayoutManager mGridLayout;
    private MovieAdapter mAdapter;
    private ArrayList<Movie> mMovies;
    private boolean deviceIsOnline;
    private boolean searchingPopular;
    private TextView tv_SortStuff;
    private TextView tv_Feedback;
    private ProgressBar pb_Loading;
    private Uri mAndroidURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myRecycler = (RecyclerView) findViewById(R.id.rv_Recycler);
        tv_Feedback = (TextView) findViewById(R.id.tv_Messages);
        tv_SortStuff = (TextView) findViewById(R.id.tv_SortOrder);
        pb_Loading = (ProgressBar)findViewById(R.id.pb_ProgressBar);

        mGridLayout = new GridLayoutManager(MainActivity.this, 2);
        myRecycler.setLayoutManager(mGridLayout);


        if(savedInstanceState == null){                 //Start up
            searchingPopular = true;
            checkInternetConnectivity();
        } else {
                searchingPopular = savedInstanceState.getBoolean("searchingPopular");
                mMovies = new ArrayList<Movie>();
                mMovies = savedInstanceState.getParcelableArrayList("moviesList");
                mAdapter = new MovieAdapter(MainActivity.this, mMovies, MainActivity.this);
                myRecycler.setAdapter(mAdapter);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putBoolean("searchingPopular", searchingPopular);
        outState.putParcelableArrayList("moviesList", mMovies);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tv_SortStuff.setText(searchingPopular? "MOST POPULAR":"TOP RATED");
    }

    @Override   //Dropdown Menu Stuff
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        return true;
    }

    @Override   //Dropdown Menu Stuff
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();
        switch (selectedItem){
            case R.id.mnu_popular:
                searchingPopular = true;
                checkInternetConnectivity();                //start over
                break;

            case R.id.mnu_topRated:
                searchingPopular = false;
                checkInternetConnectivity();                //start over
                break;
            default:
                break;
        }

        return true;
    }

    private void downloadMovies() {
        AsyncTask<String, Void, JSONObject> myNetworkTask = new AsyncTask<String, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                myRecycler.setAdapter(null);
                tv_SortStuff.setText("");
                pb_Loading.setVisibility(View.VISIBLE);
            }

            @Override
            protected JSONObject doInBackground(String... params) {

                JSONObject dJSONObj = null;

                try{
                    OkHttpClient myClient = new OkHttpClient();
                    Request myRequest = new Request.Builder().url(mAndroidURI.toString()).build();
                    Response myResponse = myClient.newCall(myRequest).execute();
                    dJSONObj = new JSONObject(myResponse.body().string());
                } catch (IOException ex){
                    ex.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return dJSONObj;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {

                mMovies = new ArrayList<Movie>();

                try {
                    JSONArray arrMovies = jsonObject.getJSONArray("results");
                    for (int i = 0; i < arrMovies.length(); i++){
                        JSONObject aMovie = arrMovies.getJSONObject(i);
                        String poster_path = MyConstants.IMAGE_BASE_URL + MyConstants.IMAGE_PREFERED_SIZE + aMovie.getString("poster_path");
                        String overview = aMovie.getString("overview");
                        boolean adult = aMovie.getBoolean("adult");
                        String release_date = aMovie.getString("release_date");
                        JSONArray genreIDs = aMovie.getJSONArray("genre_ids");
                        List<Integer> genre_ids = new ArrayList<>();
                        for(int j = 0; j < genreIDs.length(); j++){
                            genre_ids.add(genreIDs.getInt(j));
                        }

                        int id = aMovie.getInt("id");
                        String original_title = aMovie.getString("original_title");
                        String original_language = aMovie.getString("original_language");
                        String title = aMovie.getString("title");
                        String backdrop_path = MyConstants.IMAGE_BASE_URL + MyConstants.IMAGE_PREFERED_SIZE + aMovie.getString("backdrop_path");
                        double popularity = aMovie.getDouble("popularity");
                        int vote_count = aMovie.getInt("vote_count");
                        boolean video = aMovie.getBoolean("video");
                        double vote_average = aMovie.getDouble("vote_average");

                        Movie currentMovie = new Movie(poster_path, overview, adult, release_date,genre_ids, id, original_title, original_language, title, backdrop_path, popularity, vote_count, video, vote_average);
                        mMovies.add(currentMovie);
                    }

                    mGridLayout = new GridLayoutManager(MainActivity.this, 2);
                    myRecycler.setLayoutManager(mGridLayout);
                    tv_SortStuff.setText(searchingPopular? "MOST POPULAR":"TOP RATED");
                    mAdapter = new MovieAdapter(MainActivity.this, mMovies, MainActivity.this);
                    myRecycler.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();                    //important
                    pb_Loading.setVisibility(View.INVISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    tv_Feedback.setText(e.toString());
                    tv_Feedback.setVisibility(View.VISIBLE);
                }
            }
        }.execute();
    }

    @Override
    public void onMovieThumbnailClick(int clickedPos) {
        //Start the Details Page Activity here
        Movie clickedMovie = mMovies.get(clickedPos);
        Intent myIntent = new Intent(this, DetailActivity.class);
        Bundle bundleOfExtras = new Bundle();
        bundleOfExtras.putParcelable("Movie_Object", clickedMovie);
        myIntent.putExtras(bundleOfExtras);                     //Extras: PLURAL
        //myIntent.putExtra(bundleOfExtras);                    //putExtras is DIFFERENT than putExtra
        /*myIntent.putExtra("original_title", clickedMovie.original_title);
        myIntent.putExtra("poster_path", clickedMovie.backdrop_path);
        myIntent.putExtra("synopsis", clickedMovie.overview);
        myIntent.putExtra("release_date", clickedMovie.release_date);
        myIntent.putExtra("rating", clickedMovie.vote_average);
        */
        startActivity(myIntent);
    }

    private void checkInternetConnectivity(){
        buildAndroidURI();

        AsyncTask<Void, Void, Void> iNetCheckTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try{
                    int timeOut_ms = 3000;
                    Socket aSock = new Socket();
                    SocketAddress aSockAdd = new InetSocketAddress("8.8.8.8", 53);
                    aSock.connect(aSockAdd, timeOut_ms);
                    aSock.close();
                    deviceIsOnline = true;
                } catch (IOException ex){
                    deviceIsOnline = false;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(deviceIsOnline){
                    downloadMovies();
                } else {
                    startNoConnectivityActivity();
                }
            }

        }.execute();
    }

    private void startNoConnectivityActivity(){
        startActivity(new Intent(this, NoInternet.class));
    }

    private void buildAndroidURI(){
        Uri builtUri;
        if(searchingPopular){
             builtUri = Uri.parse(MyConstants.BASE_URL).buildUpon()          //.buildUpon() serves as "?" - the question mark
                    .appendQueryParameter(MyConstants.SORT_PARAM, MyConstants.POPULARITY_VALUE)
                    .appendQueryParameter(MyConstants.API_KEY, MyConstants.dAPIkey)
                    .build();
        } else {
            builtUri = Uri.parse(MyConstants.BASE_URL).buildUpon()
                    .appendQueryParameter(MyConstants.CERT_COUNTRY_KEY, MyConstants.dCertCountry)
                    .appendQueryParameter(MyConstants.CERT_TYPE_KEY, MyConstants.dCertType)
                    .appendQueryParameter(MyConstants.SORT_PARAM, MyConstants.TOP_RATE_VALUE)
                    .appendQueryParameter(MyConstants.API_KEY, MyConstants.dAPIkey)
                    .build();
        }

        mAndroidURI = builtUri;
    }

}
