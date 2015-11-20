package com.example.brad100481185.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ActivityLog extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_log);
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
