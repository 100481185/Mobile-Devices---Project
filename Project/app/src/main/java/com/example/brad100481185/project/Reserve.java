package com.example.brad100481185.project;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Reserve extends Activity {

    int quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);
        Intent event = getIntent();

        Bundle info = event.getExtras();
        quantity = Integer.parseInt(info.getString("quantity"));

        //update text view
        String in = info.getString("name") + "\n\n" + info.getString("description") + "\n\n" + "Available: " + info.getString("start") + " to " + info.getString("end") + "\n" + "Tickets Remaining: " + info.getString("quantity");
        TextView eventInfo = (TextView)findViewById(R.id.event_information);
        eventInfo.setText(in);

        //update image view
        GetBanner banner = new GetBanner();
        banner.execute(info.getString("img"));
    }

    class GetBanner extends AsyncTask<String, Void, Drawable> {
        private Drawable banner;

        protected Drawable doInBackground(String... params){
            try{
                InputStream is = (InputStream) new URL(params[0]).getContent();
                banner = Drawable.createFromStream(is, "banner");
            } catch(Exception e){
                e.printStackTrace();
            }
            return banner;
        }

        protected void onPostExecute(Drawable d){
            ImageView img = (ImageView)findViewById(R.id.imageView);
            img.setImageDrawable(banner);
        }
    }

    public void reserve(View view){
        Intent confirmReserve = new Intent(Intent.ACTION_PICK);
        if(quantity > 0){
            quantity--;
            Bundle newQuantity = new Bundle();
            newQuantity.putString("quantity", String.valueOf(quantity));
            confirmReserve.putExtras(newQuantity);
            setResult(1, confirmReserve);
        } else {
            setResult(-1, confirmReserve);
        }
        finish(); //placed here for now
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reserve, menu);
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
