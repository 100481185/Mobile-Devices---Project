package com.example.brad100481185.project;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

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
    public String urlMAIN = "http://localize-seprojects.rhcloud.com";
    public JSONArray arr;
    public JSONObject obj;
    public int objIndex;

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

        DownloadOriginTask downloadOriginTask = new DownloadOriginTask();
        downloadOriginTask.execute(urlBASE);
    }

    private Drawable getImage(String urlIMG){
        try{
            InputStream input = (InputStream) new URL(urlIMG).getContent();
            Drawable banner = Drawable.createFromStream(input, "src name");
            return banner;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
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
                String line;
                while((line = reader.readLine()) != null){
                    sb.append(line+"\n");
                }
                in.close();
                str = sb.toString();

                //parse string to JSON array
                arr = new JSONArray(str);
                imgButton();
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

    //obtains banner url from JSON array and adds it to imageButton
    private void imgButton(){
        for(int a = 0; a < arr.length(); a++){
            try{
                String urlIMG = urlMAIN + arr.getJSONObject(a).getString("banner");
                Drawable i = getImage(urlIMG);
                if(a == 0){
                    ImageButton img = (ImageButton)findViewById(R.id.imageButton4);
                    img.setImageDrawable(i);
                } else if(a == 1){
                    ImageButton img = (ImageButton)findViewById(R.id.imageButton3);
                    img.setImageDrawable(i);
                } else if(a == 2){
                    ImageButton img = (ImageButton)findViewById(R.id.imageButton2);
                    img.setImageDrawable(i);
                } else if(a == 3){
                    ImageButton img = (ImageButton)findViewById(R.id.imageButton);
                    img.setImageDrawable(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //todo: proceed to event log activity
    public void eventLog(View view){
        Intent logIntent = new Intent(Main.this, ActivityLog.class);
        startActivity(logIntent);
    }

    //todo: proceed to preferences activity
    public void preferences(View view){
        Intent preferenceIntent = new Intent(Main.this, Preferences.class);
        startActivity(preferenceIntent);
    }

    //proceed to reservation activity
    public void eventSelected(View view) {
        Intent eventIntent = new Intent(Main.this, Reserve.class);
        Bundle event = new Bundle();
        try{
            switch(view.getId()){
                case R.id.imageButton:
                    if(arr.getJSONObject(3) != null){
                        obj = arr.getJSONObject(3);
                        objIndex = 3;
                        break;
                    } else {
                        return;
                    }
                case R.id.imageButton2:
                    if(arr.getJSONObject(2) != null){
                        obj = arr.getJSONObject(2);
                        objIndex = 2;
                        break;
                    } else {
                        return;
                    }
                case R.id.imageButton3:
                    if(arr.getJSONObject(1) != null){
                        obj = arr.getJSONObject(1);
                        objIndex = 1;
                        break;
                    } else {
                        return;
                    }
                default:
                    if(arr.getJSONObject(0) != null){
                        obj = arr.getJSONObject(0);
                        objIndex = 0;
                    } else {
                        return;
                    }
            }
            event.putString("name", obj.getString("title"));
            event.putString("description", obj.getString("description"));
            event.putString("start", obj.getString("date_of_creation"));
            event.putString("end", obj.getString("end_date"));
            event.putString("quantity", obj.getString("quantity"));
            event.putString("img", urlMAIN + obj.getString("banner"));
            eventIntent.putExtras(event);
            startActivityForResult(eventIntent, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent resultIntent) {
        super.onActivityResult(requestCode, responseCode, resultIntent);

        if(responseCode == 1){
            //update quantity of event
            try{
                obj.remove("quantity");
                obj.put("quantity", resultIntent.getStringExtra("quantity"));
                arr.put(objIndex, obj);
                //todo: send change request to server
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(responseCode == -1){
            //This event is full.
            Toast.makeText(getApplicationContext(), "This event is full.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //todo: functionality of search bar
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

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
