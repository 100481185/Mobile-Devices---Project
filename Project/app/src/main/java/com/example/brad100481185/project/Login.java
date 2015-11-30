package com.example.brad100481185.project;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login extends Activity {

    private String urlMAIN = "http://localize-seprojects.rhcloud.com/customers/sign_in.json";
    private SharedPreferences share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        share = getSharedPreferences("CurrentUser", MODE_PRIVATE);
    }

    //execute login
    public void login(View view){
        EditText name = (EditText)findViewById(R.id.editText2);
        EditText pass = (EditText)findViewById(R.id.editText3);

        String n = name.getText().toString();
        String p = pass.getText().toString();

        //fields are empty
        if(n.length() == 0 || p.length() == 0){
            Toast.makeText(getApplicationContext(), "Please complete the required fields.", Toast.LENGTH_LONG).show();
            name.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            pass.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        }
        //access granted
        else if(verify(n, p)){
            name.setText("");
            pass.setText("");
            name.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
            pass.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);

            Intent log = new Intent(Intent.ACTION_PICK);
            Bundle user = new Bundle();

            user.putString("user_email", n);
            user.putString("user_token", share.getString("AuthToken", "NA"));
            log.putExtras(user);

            setResult(2, log);
            finish();
        }
        //access denied
        else {
            Toast.makeText(getApplicationContext(), "The login information is incorrect.  Please try again.", Toast.LENGTH_LONG).show();
            name.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            pass.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        }
    }

    //check is username and password are valid
    private boolean verify(String name, String password){
        try{
            VerifyLogin v = new VerifyLogin();
            Boolean b = v.execute(urlMAIN, name, password).get();
            return b;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //verifies login information
    class VerifyLogin extends AsyncTask<String, Void, Boolean>{
        private Exception exception = null;
        private String error = null;

        private JSONObject user = new JSONObject();
        private JSONObject hold = new JSONObject();
        private JSONObject j = null;

        protected Boolean doInBackground(String... params){
            try{
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setChunkedStreamingMode(0);

                //fill in JSONObject with user data to be sent
                user.put("email", params[1]);
                user.put("password", params[2]);
                hold.put("customer", user);

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                writer.write(hold.toString());
                writer.flush();
                writer.close();

                //connection accepted
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

                    j = new JSONObject(sb.toString());
                    return true;
                }
                // connection not accepted
                else {
                    error = "Error Code "+conn.getResponseCode()+"\n "+conn.getResponseMessage();
                }
            } catch(Exception e){
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            if (exception != null) {
                exception.printStackTrace();
                return;
            }

            try{
                //add authorization token to shared preferences
                if(j != null){
                    SharedPreferences.Editor editor = share.edit();

                    editor.putString("AuthToken", j.getString("auth_token"));
                    editor.commit();

                } else {
                    //Unable to connect
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                }
            } catch(Exception e){
                e.printStackTrace();
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
