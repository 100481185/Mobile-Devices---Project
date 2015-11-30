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

public class ActivityLog extends Activity {

    String urlRES = "http://localize-seprojects.rhcloud.com/reservations.json";
    JSONArray rsrv = new JSONArray();
    JSONObject user = new JSONObject();
    private String[] events = null;
    private String[] eventAuth = null;
    private JSONArray arr = new JSONArray();

    @Override
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

            events = new String[arr.length()];
            eventAuth = new String[arr.length()];
            for(int el = 0; el < arr.length(); el++){
                try{
                    for(int ev = 0; ev < rsrv.length(); ev++){
                        if(arr.getJSONObject(el).getString("promotion_id").equalsIgnoreCase(rsrv.getJSONObject(ev).getString("id"))){
                            events[el] = rsrv.getJSONObject(ev).getString("title");
                            eventAuth[el] = arr.getJSONObject(el).getString("id");
                            break;
                        }
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
            }

            Spinner eventSpin = (Spinner)findViewById(R.id.spinner);
            ArrayAdapter<String> adapt = new ArrayAdapter<String>(ActivityLog.this, android.R.layout.simple_spinner_dropdown_item, events);
            eventSpin.setAdapter(adapt);
            eventSpin.setSelection(0);

            eventSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    TextView chosen = (TextView)findViewById(R.id.eventName);
                    chosen.setText(events[position]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }
    }

    //todo: proceed to review activity
    public void review(View view){
        TextView chosen = (TextView)findViewById(R.id.eventName);
        String name = chosen.getText().toString();

        Intent reviewIntent = new Intent(ActivityLog.this, Review.class);
        Bundle r = new Bundle();
        r.putString("name", name);
        reviewIntent.putExtras(r);
        startActivity(reviewIntent);
    }

    public void reservationCode(View view){
        String key = null;
        TextView chosen = (TextView)findViewById(R.id.eventName);
        String name = chosen.getText().toString();
        for(int ev = 0; ev < rsrv.length(); ev++){
            try{
                if(name.equalsIgnoreCase(events[ev])){
                    key = eventAuth[ev];
                    break;
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        Intent codeIntent = new Intent(ActivityLog.this, ReservationCode.class);
        Bundle k = new Bundle();
        k.putString("key", key);
        k.putString("name", name);
        codeIntent.putExtras(k);
        startActivity(codeIntent);
    }

    //todo: cancel reservation of event
    public void cancelReservation(View view){
        CancelReservation cancelled = new CancelReservation();
        cancelled.execute(urlRES);
    }

    class CancelReservation extends AsyncTask<String, Void, String>{
        private Exception exception = null;

        protected String doInBackground(String... params){
            return "";
        }
        protected void onPostExecute(String result){
            if (exception != null) {
                exception.printStackTrace();
                return;
            }
        }
    }
}
