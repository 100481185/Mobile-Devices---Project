package com.example.brad100481185.project;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

public class ActivityLog extends Activity {

    String urlRES = "http://localize-seprojects.rhcloud.com/reservations.json";
    String urlRESDel = "http://localize-seprojects.rhcloud.com/reservations/";
    JSONArray rsrv = new JSONArray();
    JSONObject user = new JSONObject();
    ArrayAdapter<String> adapt;

    private ArrayList<String> events = null;
    private ArrayList<String> eventAuth = null;
    private JSONArray arr = new JSONArray();
    private int delIndex;

    @Override
    //prepare event log page
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_log);

        Bundle r = getIntent().getExtras();
        try{
            rsrv = new JSONArray(r.getString("dat"));
            user = new JSONObject(r.getString("user"));

            GetReservations get = new GetReservations();
            get.execute(urlRES+"?customer_email="+user.getString("customer_email")+"&customer_token="+user.getString("customer_token"));
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    //obtain current reservations from reservations.json
    class GetReservations extends AsyncTask<String, Void, String> {
        private Exception exception = null;

        protected String doInBackground(String... params){
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

                //parse string to JSON array
                arr = new JSONArray(sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
                exception = e;
            }
            return "";
        }

        protected void onPostExecute(String result){
            if (exception != null) {
                exception.printStackTrace();
                return;
            }

            //set up array lists for event names and keys
            events = new ArrayList<String>();
            eventAuth = new ArrayList<String>();
            for(int el = 0; el < arr.length(); el++){
                try{
                    for(int ev = 0; ev < rsrv.length(); ev++){
                        if(arr.getJSONObject(el).getString("promotion_id").equalsIgnoreCase(rsrv.getJSONObject(ev).getString("id"))){
                            events.add(el, rsrv.getJSONObject(ev).getString("title"));
                            eventAuth.add(el, arr.getJSONObject(el).getString("id"));
                            break;
                        }
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
            }

            //prepare spinner
            Spinner eventSpin = (Spinner)findViewById(R.id.spinner);
            adapt = new ArrayAdapter<String>(ActivityLog.this, android.R.layout.simple_spinner_dropdown_item, events);
            eventSpin.setAdapter(adapt);
            eventSpin.setSelection(0);

            eventSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    TextView chosen = (TextView)findViewById(R.id.eventName);
                    chosen.setText(events.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    TextView chosen = (TextView)findViewById(R.id.eventName);
                    chosen.setText("");
                }
            });

        }
    }

    //proceed to review activity
    public void review(View view){
        TextView chosen = (TextView)findViewById(R.id.eventName);
        String name = chosen.getText().toString();

        Intent reviewIntent = new Intent(ActivityLog.this, Review.class);
        Bundle r = new Bundle();
        r.putString("name", name);
        reviewIntent.putExtras(r);
        startActivity(reviewIntent);
    }

    //proceed to reservation code activity
    public void reservationCode(View view){
        TextView chosen = (TextView)findViewById(R.id.eventName);
        String name = chosen.getText().toString();
        String key = getKey(name);

        Intent codeIntent = new Intent(ActivityLog.this, ReservationCode.class);
        Bundle k = new Bundle();
        k.putString("key", key);
        k.putString("name", name);
        codeIntent.putExtras(k);
        startActivity(codeIntent);
    }

    //obtain key for event
    private String getKey(String name){
        for(int ev = 0; ev < rsrv.length(); ev++){
            try{
                if(name.equalsIgnoreCase(events.get(ev))){
                    delIndex = ev;
                    return eventAuth.get(ev);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    //cancel a reservation
    public void cancelReservation(View view){
        TextView chosen = (TextView)findViewById(R.id.eventName);
        String name = chosen.getText().toString();
        String key = getKey(name);

        CancelReservation cancelled = new CancelReservation();
        cancelled.execute(urlRESDel + key + ".json");
    }

    //make cancellation functional (NOT SAVED IN SERVER)
    class CancelReservation extends AsyncTask<String, Void, String>{
        private Exception exception = null;
        private String error = null;

        protected String doInBackground(String... params){
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setChunkedStreamingMode(0);

                //connection accepted
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

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

        protected void onPostExecute(String result){
            if (exception != null) {
                exception.printStackTrace();
                return;
            }

            if(error.equals(null)){
                //update array lists and spinner
                events.remove(delIndex);
                eventAuth.remove(delIndex);

                Toast.makeText(getApplicationContext(), "Delete successful!", Toast.LENGTH_LONG).show();
                adapt.notifyDataSetChanged();

                TextView chosen = (TextView)findViewById(R.id.eventName);
                if(delIndex == events.size()){
                    if(events.isEmpty()){
                        chosen.setText("");
                    } else {
                        chosen.setText(events.get(delIndex-1));
                    }
                } else {
                    chosen.setText(events.get(delIndex));
                }
            } else {
                //Unable to connect
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            }

        }
    }
}
