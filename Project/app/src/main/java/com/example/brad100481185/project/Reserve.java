package com.example.brad100481185.project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

public class Reserve extends Activity {

    final int MAX = Integer.MAX_VALUE;
    int quantity;

    @Override
    // set up reservations page
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);
        Intent event = getIntent();

        Bundle info = event.getExtras();

        //set quantity of event
        String quantityString = "Unlimited";
        if (!info.getString("quantity").equals("-1") && !info.getString("quantity").equals(String.valueOf(Integer.MAX_VALUE))){
            quantity = Integer.parseInt(info.getString("quantity"));
            quantityString = info.getString("quantity");
        } else quantity = MAX;

        //determine if event has an expiration date
        String hasEnd = "";
        if(!info.getString("end").equals("null"))
            hasEnd = " to " + info.getString("end");

        //update text view
        String in = info.getString("name") + "\n\n" + info.getString("description") + "\n\n" + "Available: " + info.getString("start") + hasEnd + "\n" + "Tickets Remaining: " + quantityString + "\n" + "Cost: $" + info.getString("price");
        TextView eventInfo = (TextView)findViewById(R.id.event_information);
        eventInfo.setText(in);

        //update image view
        GetBanner banner = new GetBanner();
        banner.execute(info.getString("img"));
    }

    //obtains banner from image url
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

        //event is vacant
        if(quantity > 0){
            if(quantity != MAX) quantity--;
            Bundle newQuantity = new Bundle();
            newQuantity.putString("quantity", String.valueOf(quantity));
            confirmReserve.putExtras(newQuantity);
            setResult(1, confirmReserve);
        }
        //event is full
        else {
            setResult(-1, confirmReserve);
        }
        finish();
    }
}
