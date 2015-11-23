package com.example.brad100481185.project;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

public class WriteReview extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);
    }

    public void saveReview(View view){
        //todo: save review
        EditText rev = (EditText)findViewById(R.id.editText);
        String review = rev.getText().toString();
        rev.setText("");

        RatingBar star = (RatingBar)findViewById(R.id.ratingBar);
        int rate = star.getNumStars();
        star.setNumStars(0);
        finish();
    }

    public void cancelReview(View view){
        EditText rev = (EditText)findViewById(R.id.editText);
        rev.setText("");

        RatingBar star = (RatingBar)findViewById(R.id.ratingBar);
        star.setNumStars(0);
        finish();
    }
}
