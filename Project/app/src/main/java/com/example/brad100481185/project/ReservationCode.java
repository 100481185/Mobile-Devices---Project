package com.example.brad100481185.project;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.SearchView;
import android.widget.TextView;

public class ReservationCode extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_code);

        TextView name = (TextView)findViewById(R.id.name_code);
        TextView code = (TextView)findViewById(R.id.code);

        name.setText(getIntent().getExtras().getString("name"));
        code.setText(getIntent().getExtras().getString("key"));
    }

    //todo: add functionality

}
