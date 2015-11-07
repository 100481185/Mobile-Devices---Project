package com.example.brad100481185.project;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EventSearch extends Activity {

    public JSONArray arr;
    public String urlBASE = "http://localize-seprojects.rhcloud.com/promotions.json";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_search);
        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            ObtainDataTask data = new ObtainDataTask();
            data.execute(urlBASE, query);
        }
    }

    //todo: display events with search query; go to reservation activity from there
    class ObtainDataTask extends AsyncTask<String, Void, String>{
        private Exception exception = null;
        private String str = null;
        private String query = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                query = params[1];

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

            try{
                for(int a = 0; a < arr.length(); a++){
                    if(arr.getJSONObject(a).getString("title").contains(query.toLowerCase())) {
                        Log.i("T", arr.getJSONObject(a).getString("title"));
                    } else if(arr.getJSONObject(a).getString("description").contains(query.toLowerCase())){
                        Log.i("D", arr.getJSONObject(a).getString("title"));
                    } else {
                        Log.i("N", query + " is not here");
                    }
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
