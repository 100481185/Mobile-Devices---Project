package com.example.brad100481185.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Review extends Activity {

    ArrayList<String> reviews = new ArrayList<String>();
    ListView reviewList;
    ArrayAdapter<String> reviewAdapt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        //todo: post request to obtain actual reviews for events
        reviewList = (ListView)findViewById(R.id.listView2);
        reviewAdapt = new ArrayAdapter<String>(Review.this, android.R.layout.simple_list_item_1, reviews);
        reviewList.setAdapter(reviewAdapt);
    }

    public void write(View view){
        Intent writeIntent = new Intent(Review.this, WriteReview.class);
        startActivityForResult(writeIntent, 1);
    }

    public void onActivityResult(int requestCode, int responseCode, Intent resultIntent){
        super.onActivityResult(requestCode, responseCode, resultIntent);

        if(responseCode == 1){
            Bundle rev = resultIntent.getExtras();
            reviews.add(rev.getString("review")+"\n"+rev.getString("rating")+" stars");
            reviewAdapt.notifyDataSetChanged();
        }
    }
}
