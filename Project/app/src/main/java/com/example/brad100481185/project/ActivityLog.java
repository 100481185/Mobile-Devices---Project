package com.example.brad100481185.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

public class ActivityLog extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_log);

        Spinner events = (Spinner)findViewById(R.id.spinner);
        /*
        ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_drpodown, array);
        events.setAdapter(adapt);
        events.setSelection(0);
         */

        events.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView chosen = (TextView)findViewById(R.id.eventName);
                //chosen.setText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //todo: proceed to review activity
    public void review(View view){
        Intent reviewIntent = new Intent(ActivityLog.this, Review.class);
        startActivity(reviewIntent);
    }

    //todo: proceed to reservation code activity
    public void reservationCode(View view){
        Intent codeIntent = new Intent(ActivityLog.this, ReservationCode.class);
        startActivity(codeIntent);
    }

    //todo: cancel reservation of event
    public void cancelReservation(View view){
        //do something
    }
}
