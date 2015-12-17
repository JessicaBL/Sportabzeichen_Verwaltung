package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {

    private String username, password;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    SessionManager session;
    String ip_port;
    String LOGIN_URL;

    private static final String RESPONSE_SUCCESS = "success";
    private static final String RESPONSE_MESSAGE = "message";
    private static final String RESPONSE_PRUEFER = "pruefer";
    private static final String RESPONSE_PRUEFER_ID = "id";
    private static final String RESPONSE_PRUEFER_NAME = "name";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ip_port = getApplicationContext().getString(R.string.ip_port);
        LOGIN_URL = "http://" + ip_port + "/sportabzeichen/login.php";

        session = new SessionManager(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("DSA-App");
        getSupportActionBar().setSubtitle("Login");

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
        if (id == R.id.action_login) {

            EditText user = (EditText)findViewById(R.id.editText_pruefer_name);
            EditText pwd = (EditText)findViewById(R.id.editText_pruefer_password);
            username = user.getText().toString();
            password = pwd.getText().toString();

            //Execute AsyncTask to validate login data on remote db
            LoginTask login = new LoginTask();
            login.execute();

        }

        if(id==R.id.action_close_app){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch(keycode) {
            case KeyEvent.KEYCODE_BACK:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("App beenden?");
                builder.setCancelable(false);
                builder.setPositiveButton("Beenden", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);

                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();


                return true;
        }

        return super.onKeyDown(keycode, e);
    }

    class LoginTask extends AsyncTask<String, String, String> {

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Show progress dialog during task execution
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Login..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            int success;

            try {

                //Put login credentials in parameter list
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));

                //Execute HTTP Request
                JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST", params);

                Log.d("Check credentials..", json.toString());

                //Get success value of HTTP Response
                success = json.getInt(RESPONSE_SUCCESS);

                //Return HTTP Response message to AsyncTask to use this in onPostExecute method
                if (success == 1) {
                    Log.d("Login successful!", json.toString());

                    String pruefer_id = null;
                    String pruefer_name = null;

                    try{
                        //Get JSON object from HTTP Response and set values for id and name
                        JSONArray pruefer = json.getJSONArray(RESPONSE_PRUEFER);
                        JSONObject p = pruefer.getJSONObject(0);

                        pruefer_id = p.getString(RESPONSE_PRUEFER_ID);
                        pruefer_name = p.getString(RESPONSE_PRUEFER_NAME);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    session.createLoginSession(pruefer_id, pruefer_name);

                    return pruefer_name;

                } else {
                    Log.d("Login failed!", json.toString());
                    //If login failed set failure to true
                    failure = true;

                    return json.getString(RESPONSE_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String response) {

            //Close progress dialog
            pDialog.dismiss();

            //If login failed show HTTP Response message and stay on login activity, else go on to main activity
            if (!failure) {

                Toast.makeText(getApplicationContext(), "Hallo " + response , Toast.LENGTH_LONG).show();
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(main);
                finish();

            } else {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            }
        }
    }
}

