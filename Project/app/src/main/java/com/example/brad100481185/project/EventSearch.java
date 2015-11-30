package com.example.brad100481185.project;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class EventSearch extends Activity {

    public JSONArray arr;
    public int objIndex;
    public JSONObject obj;
    public JSONObject user = new JSONObject();
    public String urlBASE = "http://localize-seprojects.rhcloud.com/promotions.json";
    public String urlRES = "http://localize-seprojects.rhcloud.com/reservations.json";
    public String urlMAIN = "http://localize-seprojects.rhcloud.com";

    ArrayList<String> res = new ArrayList<String>();
    private boolean loggedIn = false;
    ListView list;
    private String newQuantity;
    private String email, token, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_search);
        handleIntent(getIntent());

        // set up list of search results
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

    // start up reserve activity from search results
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
            event.putString("price", obj.getString("price"));

            eventIntent.putExtras(event);
            startActivityForResult(eventIntent, 1);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent resultIntent) {
        super.onActivityResult(requestCode, responseCode, resultIntent);

        //successful reservation
        if(responseCode == 1){
            //update quantity of event
            try{
                newQuantity = resultIntent.getStringExtra("quantity");
                id = obj.getString("id");

                if(!loggedIn){ //not logged in yet
                    startActivityForResult(new Intent(this, Login.class), 2);
                } else { //already logged in
                    reserveRequest();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //return from login
        else if(responseCode == 2) {
            loggedIn = true;
            Bundle logged = resultIntent.getExtras();

            email = logged.getString("user_email");
            token = logged.getString("user_token");

            if(requestCode == 2) reserveRequest();
            else if(requestCode == 4) Toast.makeText(getApplicationContext(), "This event is full.", Toast.LENGTH_LONG).show();
        }
        //the event is full
        else if(responseCode == -1){
            if(!loggedIn){ //not logged in yet
                startActivityForResult(new Intent(this, Login.class), 4);
            } else { //already logged in
                Toast.makeText(getApplicationContext(), "This event is full.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // process request to server to confirm reservation
    private void reserveRequest(){
        try{
            ChangeQuantityTask change = new ChangeQuantityTask();

            user.put("promotion_id", obj.getString("id"));
            user.put("customer_email", email);
            user.put("customer_token", token);

            Log.i("U", user.toString());
            change.execute(urlRES, newQuantity, user.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // changes quantity remaining for event
    class ChangeQuantityTask extends AsyncTask<String, Void, String>{
        private Exception exception = null;
        private String resp = null;
        private String quantity = null;
        private String error = null;
        private JSONObject response = null;

        protected String doInBackground(String... params){
            try {
                quantity = params[1];

                //get JSON from url
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setChunkedStreamingMode(0);

                // write account information to connection
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                writer.write(params[2]);
                writer.flush();
                writer.close();

                // code 200 - ok
                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
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
                }
                // connection not accepted
                else {
                    error = "Error Code "+conn.getResponseCode()+"\n "+conn.getResponseMessage();
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
            try{
                // quantity is changed if the connection does not fail
                if(response != null){
                    // quantity change is allowed
                    if(response.getString("status").equalsIgnoreCase("ok")){
                        obj.remove("quantity");
                        obj.put("quantity", quantity);
                        arr.put(objIndex, obj);
                        Toast.makeText(getApplicationContext(), response.getString("message").replace("[", "").replace("]", "").replace("\"", ""), Toast.LENGTH_LONG).show();
                    }
                    // quantity changed is not allowed
                    else {
                        if(response.getString("errors").contains("valid_to")){
                            Toast.makeText(getApplicationContext(), response.getJSONObject("errors").getString("valid_to").replace("[", "").replace("]", "").replace("\"", ""), Toast.LENGTH_LONG).show();
                        } else if(response.getString("errors").contains("reservation")){
                            Toast.makeText(getApplicationContext(), response.getJSONObject("errors").getString("reservation").replace("[", "").replace("]", "").replace("\"", ""), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    //Unable to connect
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
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
        private String error = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                query = params[1];

                //get JSON from url
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setDoInput(true);
                conn.setChunkedStreamingMode(0);

                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
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
                }
                // connection not accepted
                else {
                    error = "Error Code "+conn.getResponseCode()+"\n "+conn.getResponseMessage();
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

            if(error == null){
                try{
                    //search json data for query
                    int index = 0;
                    TextView found = (TextView)findViewById(R.id.failed);
                    for(int a = 0; a < arr.length(); a++){
                        if(arr.getJSONObject(a).getString("title").toLowerCase().contains(query.toLowerCase())) {
                            res.add(index, arr.getJSONObject(a).getString("title"));
                            index++;
                        } else if(arr.getJSONObject(a).getString("description").toLowerCase().contains(query.toLowerCase())){
                            res.add(index, arr.getJSONObject(a).getString("title"));
                            index++;
                        }
                    } if(res.isEmpty()){
                        found.setText("No results found for " + query);
                    } else {
                        found.setText(index + " result(s) found for " + query);
                        ArrayAdapter<String> adapt = new ArrayAdapter<String>(EventSearch.this,android.R.layout.simple_list_item_1, res);
                        list.setAdapter(adapt);
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
            } else {
                //Unable to connect
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            }
        }
    }
}
