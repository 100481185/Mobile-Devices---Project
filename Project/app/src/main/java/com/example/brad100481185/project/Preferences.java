package com.example.brad100481185.project;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class Preferences extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
    }

    //todo: pressing the save button
    public void save(View view){
        //do something
        finish();
    }

    //cancel changes made
    public void cancel(View view){
        finish();
    }
}
