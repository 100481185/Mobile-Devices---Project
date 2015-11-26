package com.example.brad100481185.project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login extends Activity {

    public String urlMAIN = "http://localize-seprojects.rhcloud.com";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view){
        EditText name = (EditText)findViewById(R.id.editText2);
        EditText pass = (EditText)findViewById(R.id.editText3);

        String n = name.getText().toString();
        String p = pass.getText().toString();

        if(verify(n, p)){
            name.setText("");
            pass.setText("");
            name.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            pass.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

            Intent log = new Intent(Intent.ACTION_PICK);
            setResult(2, log);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "The login information is incorrect.  Please try again.", Toast.LENGTH_LONG).show();
            name.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            pass.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        }
    }

    private boolean verify(String name, String password){
        try{
            VerifyLogin v = new VerifyLogin();
            return v.execute(name, password).get();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    class VerifyLogin extends AsyncTask<String, Void, Boolean>{
        private Exception exception = null;

        protected Boolean doInBackground(String... params){
            //todo: verify that login information is correct
            return false;
        }

        protected void onPostExecute(Boolean result) {
            if (exception != null) {
                exception.printStackTrace();
                return;
            }
            super.onPostExecute(result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
