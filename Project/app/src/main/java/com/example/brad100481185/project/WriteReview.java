package com.example.brad100481185.project;

import android.app.Activity;
import android.content.Intent;
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

    //saves review dialogs and empties them afterwards
    public void saveReview(View view){
        Intent save = new Intent(Intent.ACTION_PICK);
        Bundle dat = new Bundle();

        EditText rev = (EditText)findViewById(R.id.editText);
        String review = rev.getText().toString();
        rev.setText("");
        dat.putString("review", review);

        RatingBar star = (RatingBar)findViewById(R.id.ratingBar);
        int rate = star.getNumStars();
        star.setNumStars(0);
        dat.putString("rating", String.valueOf(rate));

        save.putExtras(dat);
        setResult(1, save);
        finish();
    }

    //empties review dialogs and disregards input
    public void cancelReview(View view){
        EditText rev = (EditText)findViewById(R.id.editText);
        rev.setText("");

        RatingBar star = (RatingBar)findViewById(R.id.ratingBar);
        star.setNumStars(0);

        setResult(0);
        finish();
    }
}
