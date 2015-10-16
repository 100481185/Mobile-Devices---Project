package com.example.brad100481185.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class Main extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //functionality for drop down menu
        final Spinner dropdown = (Spinner)findViewById(R.id.dropdownmenu);
        String[] items = new String[]{"--select--", "Event Log", "Preferences"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(dropdown.getSelectedItemPosition() == 1) eventLog(view);
                else if(dropdown.getSelectedItemPosition() == 2) preferences(view);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //proceed to event log activity
    public void eventLog(View view){
        Intent logIntent = new Intent(Main.this, ActivityLog.class);
        startActivity(logIntent);
    }

    //proceed to preferences activity
    public void preferences(View view){
        Intent preferenceIntent = new Intent(Main.this, Preferences.class);
        startActivity(preferenceIntent);
    }

    //proceed to reservation activity
    public void eventSelected(View view){
        Intent eventIntent = new Intent(Main.this, Reserve.class);
        startActivity(eventIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
