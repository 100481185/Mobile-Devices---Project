package com.example.brad100481185.project;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
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

            imgButton();
        }
    }

    private Drawable getImage(String urlIMG){
        try{
            GetImageTask banner = new GetImageTask();
            return banner.execute(urlIMG).get();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

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

    //todo: proceed to event log activity
    public void eventLog(MenuItem item){
        Intent logIntent = new Intent(Main.this, ActivityLog.class);
        startActivity(logIntent);
    }

    //todo: proceed to preferences activity
    public void preferences(MenuItem item){
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
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}
