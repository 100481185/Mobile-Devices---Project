package com.example.brad100481185.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_log, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
