package com.example.brad100481185.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Review extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
    }

    //todo: add functionality
    public void write(View view){
        Intent writeIntent = new Intent(Review.this, WriteReview.class);
        startActivity(writeIntent);
    }
}
