package com.example.brad100481185.project;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class Main extends Activity implements LocationListener {

    public String urlBASE = "http://localize-seprojects.rhcloud.com/promotions.json";
    public String urlRES = "http://localize-seprojects.rhcloud.com/reservations.json";
    public String urlMAIN = "http://localize-seprojects.rhcloud.com";
    public JSONArray arr;
    public JSONObject obj;
    public JSONObject user = new JSONObject();

    public int objIndex;
    public static boolean loggedIn = false;

    private double latitude, longitude;
    private String newQuantity;
    private String email, token, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // obtain current location
        requestLocationPermissions();
        obtainLocation();
    }

    // request permission for fine location
    final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 410020;
    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Explain to the user why we need to read the contacts
            }

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_LOCATION);

            return;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    obtainLocation();
                } else {
                    // tell the user that the feature will not work
                }
                return;
            }
        }
    }

    // obtain current location
    private void obtainLocation(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

            // request an fine location provider
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setSpeedRequired(false);
            criteria.setCostAllowed(false);
            String recommended = locationManager.getBestProvider(criteria, true);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);

            Location location = locationManager.getLastKnownLocation(recommended);
            if (location != null) {
                if (Geocoder.isPresent()) {
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());

                    try {
                        // reverse geocode from current GPS position
                        List<Address> results = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                        if (results.size() > 0) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            ObtainEvents obtain = new ObtainEvents();
                            obtain.execute(urlBASE);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void onProviderEnabled(String provider) {

    }

    public void onProviderDisabled(String provider) {

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void onLocationChanged(Location location) {
        obtainLocation();
    }

    // obtain the events from promotions.json
    class ObtainEvents extends AsyncTask<String, Void, String> {
        private Exception exception = null;
        private String str = null;
        private String error = null;

        @Override
        protected String doInBackground(String... params) {
            try {
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

                    /*sort by how close an event is to location
                     (involves private doubles latitude and longitude)
                     (NOT IMPLEMENTED)
                    */

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
                imgButton();
            } else {
                //Unable to connect
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            }
        }
    }

    // get image from image url provided by promotions.json
    private Drawable getImage(String urlIMG){
        try{
            GetImageTask banner = new GetImageTask();
            return banner.execute(urlIMG).get();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    // draw the image
    class GetImageTask extends AsyncTask<String, Void, Drawable>{
        private Drawable img = null;

        protected Drawable doInBackground(String... params){
            try{
                InputStream input = (InputStream) new URL(params[0]).getContent();
                this.img = Drawable.createFromStream(input, "src name");
            } catch (Exception e){
                e.printStackTrace();
            }
            return this.img;
        }
    }

    //obtains banner url from JSON array and adds it to imageButton
    private void imgButton(){
        for(int a = 0; a < arr.length(); a++){
            try{
                String urlIMG = urlMAIN + arr.getJSONObject(a).getString("banner");
                Drawable i = getImage(urlIMG);

                // append image to an imageButton
                // this is the most efficient way despite looking cluttered
                if(a == 0){
                    TextView cap = (TextView)findViewById(R.id.caption);
                    cap.setText(arr.getJSONObject(a).getString("title") + "\nPrice: $" + arr.getJSONObject(a).getString("price"));

                    ImageButton img = (ImageButton)findViewById(R.id.imageButton);
                    img.setImageDrawable(i);
                } else if(a == 1){
                    TextView cap = (TextView)findViewById(R.id.caption2);
                    cap.setText(arr.getJSONObject(a).getString("title") + "\nPrice: $" + arr.getJSONObject(a).getString("price"));

                    ImageButton img = (ImageButton)findViewById(R.id.imageButton2);
                    img.setImageDrawable(i);
                } else if(a == 2){
                    TextView cap = (TextView)findViewById(R.id.caption3);
                    cap.setText(arr.getJSONObject(a).getString("title") + "\nPrice: $" + arr.getJSONObject(a).getString("price"));

                    ImageButton img = (ImageButton)findViewById(R.id.imageButton3);
                    img.setImageDrawable(i);
                } else if(a == 3){
                    TextView cap = (TextView)findViewById(R.id.caption4);
                    cap.setText(arr.getJSONObject(a).getString("title") + "\nPrice: $" + arr.getJSONObject(a).getString("price"));

                    ImageButton img = (ImageButton)findViewById(R.id.imageButton4);
                    img.setImageDrawable(i);
                } else if(a == 4){
                    TextView cap = (TextView)findViewById(R.id.caption5);
                    cap.setText(arr.getJSONObject(a).getString("title") + "\nPrice: $" + arr.getJSONObject(a).getString("price"));

                    ImageButton img = (ImageButton)findViewById(R.id.imageButton5);
                    img.setImageDrawable(i);
                } else if(a == 5){
                    TextView cap = (TextView)findViewById(R.id.caption6);
                    cap.setText(arr.getJSONObject(a).getString("title") + "\nPrice: $" + arr.getJSONObject(a).getString("price"));

                    ImageButton img = (ImageButton)findViewById(R.id.imageButton6);
                    img.setImageDrawable(i);
                } else if(a == 6){
                    TextView cap = (TextView)findViewById(R.id.caption7);
                    cap.setText(arr.getJSONObject(a).getString("title") + "\nPrice: $" + arr.getJSONObject(a).getString("price"));

                    ImageButton img = (ImageButton)findViewById(R.id.imageButton7);
                    img.setImageDrawable(i);
                } else if(a == 7){
                    TextView cap = (TextView)findViewById(R.id.caption8);
                    cap.setText(arr.getJSONObject(a).getString("title") + "\nPrice: $" + arr.getJSONObject(a).getString("price"));

                    ImageButton img = (ImageButton)findViewById(R.id.imageButton8);
                    img.setImageDrawable(i);
                } else if(a == 8){
                    TextView cap = (TextView)findViewById(R.id.caption9);
                    cap.setText(arr.getJSONObject(a).getString("title") + "\nPrice: $" + arr.getJSONObject(a).getString("price"));

                    ImageButton img = (ImageButton)findViewById(R.id.imageButton9);
                    img.setImageDrawable(i);
                } else if(a == 9){
                    TextView cap = (TextView)findViewById(R.id.caption10);
                    cap.setText(arr.getJSONObject(a).getString("title") + "\nPrice: $" + arr.getJSONObject(a).getString("price"));

                    ImageButton img = (ImageButton)findViewById(R.id.imageButton10);
                    img.setImageDrawable(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // proceed to event log activity
    public void eventLog(MenuItem item){
        if(loggedIn){
            Intent logIntent = new Intent(Main.this, ActivityLog.class);
            Bundle e = new Bundle();
            e.putString("dat", arr.toString());
            try{
                JSONObject u = new JSONObject();
                u.put("customer_email", email);
                u.put("customer_token", token);
                e.putString("user", u.toString());
            } catch(Exception ex){
                ex.printStackTrace();
            }
            logIntent.putExtras(e);
            startActivity(logIntent);
        } else {
            Toast.makeText(getApplicationContext(), "You need to log in first.", Toast.LENGTH_LONG).show();
        }
    }

    //proceed to preferences activity (NOT IMPLEMENTED)
    public void preferences(MenuItem item){
        if(loggedIn){
            Intent preferenceIntent = new Intent(Main.this, Preferences.class);
            startActivity(preferenceIntent);
        } else {
            Toast.makeText(getApplicationContext(), "You need to log in first.", Toast.LENGTH_LONG).show();
        }
    }

    // proceed to login activity from the main activity
    public void login(MenuItem item){
        startActivityForResult(new Intent(this, Login.class), 1);
    }

    //proceed to reservation activity
    public void eventSelected(View view) {
        Intent eventIntent = new Intent(Main.this, Reserve.class);
        Bundle event = new Bundle();
        try{
            //determine object based on which button is pressed
            switch(view.getId()){
                case R.id.imageButton:
                    obj = arr.getJSONObject(0);
                    objIndex = 0;
                    break;
                case R.id.imageButton2:
                    obj = arr.getJSONObject(1);
                    objIndex = 1;
                    break;
                case R.id.imageButton3:
                    obj = arr.getJSONObject(2);
                    objIndex = 2;
                    break;
                case R.id.imageButton4:
                    obj = arr.getJSONObject(3);
                    objIndex = 3;
                    break;
                case R.id.imageButton5:
                    obj = arr.getJSONObject(4);
                    objIndex = 4;
                    break;
                case R.id.imageButton6:
                    obj = arr.getJSONObject(5);
                    objIndex = 5;
                    break;
                case R.id.imageButton7:
                    obj = arr.getJSONObject(6);
                    objIndex = 6;
                    break;
                case R.id.imageButton8:
                    obj = arr.getJSONObject(7);
                    objIndex = 7;
                    break;
                case R.id.imageButton9:
                    obj = arr.getJSONObject(8);
                    objIndex = 8;
                    break;
                default:
                    obj = arr.getJSONObject(9);
                    objIndex = 9;
            }
            event.putString("name", obj.getString("title"));
            event.putString("description", obj.getString("description"));
            event.putString("start", obj.getString("valid_from"));
            event.putString("end", obj.getString("valid_to"));
            event.putString("quantity", obj.getString("quantity"));
            event.putString("img", urlMAIN + obj.getString("banner"));
            event.putString("price", obj.getString("price"));
            eventIntent.putExtras(event);
            startActivityForResult(eventIntent, 1);
        } catch (Exception e) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        ComponentName cn = new ComponentName(this, EventSearch.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId()) {
            case R.id.preferences:
                preferences(item);
                break;
            case R.id.activity_log:
                eventLog(item);
                break;
            case R.id.logout:
                loggedIn = false;
                break;
            case R.id.login:
                login(item);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    // prepare options menu; item availability depends on whether the user is logged in
    public boolean onPrepareOptionsMenu(Menu menu){
        MenuItem logOut = menu.findItem(R.id.logout);
        MenuItem logIn = menu.findItem(R.id.login);
        MenuItem pref = menu.findItem(R.id.preferences);
        MenuItem act = menu.findItem(R.id.activity_log);

        if(loggedIn){
            pref.setVisible(true);
            act.setVisible(true);
            logOut.setVisible(true);
            logIn.setVisible(false);
        } else {
            pref.setVisible(false);
            act.setVisible(false);
            logOut.setVisible(false);
            logIn.setVisible(true);
        }
        return true;
    }
}
