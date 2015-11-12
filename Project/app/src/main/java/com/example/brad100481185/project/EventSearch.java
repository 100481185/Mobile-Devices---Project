package com.example.brad100481185.project;

import android.app.Activity;
import android.app.SearchManager;
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
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class EventSearch extends Activity {

    public JSONArray arr;
    public int objIndex;
    public JSONObject obj;
    public String urlBASE = "http://localize-seprojects.rhcloud.com/promotions.json";
    public String urlMAIN = "http://localize-seprojects.rhcloud.com";

    ArrayList<String> res = new ArrayList<String>();
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_search);
        handleIntent(getIntent());

        list = (ListView)findViewById(R.id.listView);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Object o = list.getItemAtPosition(position);
                String ob = o.toString();
                try {
                    for (int a = 0; a < arr.length(); a++) {
                        if (arr.getJSONObject(a).getString("title").toLowerCase().contains(ob.toLowerCase())) {
                            obj = arr.getJSONObject(a);
                            objIndex = a;
                            reserve(arg1);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void reserve(View view){
        try{
            Intent eventIntent = new Intent(EventSearch.this, Reserve.class);
            Bundle event = new Bundle();

            event.putString("name", obj.getString("title"));
            event.putString("description", obj.getString("description"));
            event.putString("start", obj.getString("valid_from"));
            event.putString("end", obj.getString("valid_to"));
            event.putString("quantity", obj.getString("quantity"));
            event.putString("img", urlMAIN + obj.getString("banner"));

            eventIntent.putExtras(event);
            startActivityForResult(eventIntent, 1);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent resultIntent) {
        super.onActivityResult(requestCode, responseCode, resultIntent);

        if(responseCode == 1){
            //update quantity of event
            try{
                String urlResponse = urlMAIN + "/promotions/" + obj.getString("id") + "/reserve.json";

                ChangeQuantityTask change = new ChangeQuantityTask();
                change.execute(urlResponse, resultIntent.getStringExtra("quantity"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(responseCode == -1){
            //This event is full.
            Toast.makeText(getApplicationContext(), "This event is full.", Toast.LENGTH_LONG).show();
        }
    }

    class ChangeQuantityTask extends AsyncTask<String, Void, String>{
        private Exception exception = null;
        private String resp = null;
        private String quantity = null;
        private JSONObject response = null;

        protected String doInBackground(String... params){
            try {
                quantity = params[1];

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
                resp = sb.toString();

                response = new JSONObject(resp);
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
                if(response.getString("status").equalsIgnoreCase("ok")){
                    obj.remove("quantity");
                    obj.put("quantity", quantity);
                    arr.put(objIndex, obj);
                    Toast.makeText(getApplicationContext(), "Reservation success", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Reservation failed", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                exception = e;
            }
        }
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            ObtainDataTask data = new ObtainDataTask();
            data.execute(urlBASE, query);
        }
    }

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
                //search json data for query
                int index = 0;
                for(int a = 0; a < arr.length(); a++){
                    if(arr.getJSONObject(a).getString("title").toLowerCase().contains(query.toLowerCase())) {
                        res.add(index, arr.getJSONObject(a).getString("title"));
                        index++;
                    } else if(arr.getJSONObject(a).getString("description").toLowerCase().contains(query.toLowerCase())){
                        res.add(index, arr.getJSONObject(a).getString("title"));
                        index++;
                    }
                }
                ArrayAdapter<String> adapt = new ArrayAdapter<String>(EventSearch.this,android.R.layout.simple_list_item_1, res);
                list.setAdapter(adapt);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
