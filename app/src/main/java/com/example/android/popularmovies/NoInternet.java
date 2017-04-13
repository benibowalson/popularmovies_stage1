package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class NoInternet extends AppCompatActivity  {

    private ImageButton mRetryButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        mRetryButton = (ImageButton)findViewById(R.id.ib_retry);
        mRetryButton.setOnClickListener(mListener);
    }


    final View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ib_retry:
                    startActivity(new Intent(NoInternet.this, MainActivity.class));
                    break;
                default:
                    break;
            }
        }
    };
}
