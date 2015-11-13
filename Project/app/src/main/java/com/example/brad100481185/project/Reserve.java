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

    final int MAX = Integer.MAX_VALUE;
    int quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);
        Intent event = getIntent();

        Bundle info = event.getExtras();

        String quantityString = "Unlimited";
        if (!info.getString("quantity").equals("-1") && !info.getString("quantity").equals(String.valueOf(Integer.MAX_VALUE))){
            quantity = Integer.parseInt(info.getString("quantity"));
            quantityString = info.getString("quantity");
        } else quantity = MAX;

        String hasEnd = "";
        if(!info.getString("end").equals("null"))
            hasEnd = " to " + info.getString("end");

        //update text view
        String in = info.getString("name") + "\n\n" + info.getString("description") + "\n\n" + "Available: " + info.getString("start") + hasEnd + "\n" + "Tickets Remaining: " + quantityString;
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
            if(quantity != MAX) quantity--;
            Bundle newQuantity = new Bundle();
            newQuantity.putString("quantity", String.valueOf(quantity));
            confirmReserve.putExtras(newQuantity);
            setResult(1, confirmReserve);
        } else {
            setResult(-1, confirmReserve);
        }
        finish(); //placed here for now
    }
}
