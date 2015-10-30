package com.example.brad100481185.project;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main extends Activity {

    public String urlBASE = "http://localize-seprojects.rhcloud.com/promotions.json";
    public JSONArray arr;
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
                if (dropdown.getSelectedItemPosition() == 1) eventLog(view);
                else if (dropdown.getSelectedItemPosition() == 2) preferences(view);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //functionality for search bar (to do)
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) findViewById(R.id.searchView);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        searchView.setSearchableInfo(searchableInfo);

        DownloadOriginTask downloadOriginTask = new DownloadOriginTask();
        downloadOriginTask.execute(urlBASE);
    }
    class DownloadOriginTask extends AsyncTask<String, Void, String> {
        private Exception exception = null;
        private String str = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                //get JSON from url
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setDoInput(true);
                conn.setChunkedStreamingMode(0);

                //read JSON file from inputStream
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while((line = reader.readLine()) != null){
                    sb.append(line+"\n");
                }
                in.close();
                str = sb.toString();

                //parse string to JSON array
                arr = new JSONArray(str);

                for(int i=0; i<arr.length(); i++){
                    JSONObject c = arr.getJSONObject(i);

                    String id = c.getString("id");
                    String title = c.getString("title");
                    String description = c.getString("description");

                    Log.i("JSON", id+" "+title+" "+description);
                }
            } catch (Exception e) {
                e.printStackTrace();
                exception = e;
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            if (exception != null) {
                exception.printStackTrace();
                return;
            }
        }
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
    public void eventSelected(View view) {
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
