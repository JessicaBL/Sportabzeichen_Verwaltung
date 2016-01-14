package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DSA extends AppCompatActivity {

    String endurance, strength, agility, coordination, athlete_id, total_medal;
    String medal_endurance, medal_strength, medal_agility, medal_coordination;
    DatabaseHelper myDb;
    DatabaseHelperSports myDbSp;
    DatabaseHelperResults myDbRs;

    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    String ip_port;
    String URL;
    private static final String RESPONSE_SUCCESS = "success";
    private static final String RESPONSE_MESSAGE = "message";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ds);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDb = new DatabaseHelper(this);
        myDbSp = new DatabaseHelperSports(this);
        myDbRs = new DatabaseHelperResults(this);

        ip_port = getApplicationContext().getString(R.string.ip_port);
        URL = "http://" + ip_port + "/sportabzeichen/dsa.php";

        Intent intent = getIntent();
        endurance = intent.getStringExtra("endurance");
        strength = intent.getStringExtra("strength");
        agility = intent.getStringExtra("agility");
        coordination = intent.getStringExtra("coordination");
        athlete_id = intent.getStringExtra("athlete");


        getAthleteInfos(athlete_id);

        TextView pr1=(TextView) findViewById(R.id.textView_pr1);
        TextView pr2=(TextView) findViewById(R.id.textView_pr2);
        TextView pr3=(TextView) findViewById(R.id.textView_pr3);
        TextView pr4=(TextView) findViewById(R.id.textView_pr4);
        String[]endurance_split=endurance.split(",");
        String[]strength_split=strength.split(",");
        String[]agility_split=agility.split(",");
        String[]coordination_split=coordination.split(",");

        pr1.setText(endurance_split[0]);
        pr2.setText(strength_split[0]);
        pr3.setText(agility_split[0]);
        pr4.setText(coordination_split[0]);

        medal_endurance = endurance_split[2];
        medal_strength = strength_split[2];
        medal_agility = agility_split[2];
        medal_coordination = coordination_split[2];

        int sum_medal=calculate_medal(endurance_split[2], strength_split[2],agility_split[2], coordination_split[2]);

        TextView medal=(TextView) findViewById(R.id.textView_medal);

        if(sum_medal<8){
            total_medal = "Bronze";
            medal.setText("Bronze ("+sum_medal+" Punkte)");
        }
        else if(sum_medal<11){
            total_medal = "Silber";
            medal.setText("Silber ("+sum_medal+" Punkte)");
        }
        else {
            total_medal = "Gold";
            medal.setText("Gold ("+sum_medal+" Punkte)");
        }

        DSATask dsaTask = new DSATask();
        dsaTask.execute();

    }




    public void getAthleteInfos(String athlete_id) {
        TextView athlete_name = (TextView) findViewById(R.id.textView_name);
        TextView athlete_birthday = (TextView) findViewById(R.id.textView_geb);

        Cursor res = myDb.selectSingleDataAthlete(athlete_id);
        res.moveToFirst();
        String name = res.getString(1);
        String surname = res.getString(2);
        String birthday = res.getString(4);

        String infos = name+" "+ surname;
        athlete_name.setText(infos);
        athlete_birthday.setText(birthday);
    }

    public  int calculate_medal(String endurance, String strength, String agility, String coordination) {
        int sum=0;
        if (endurance.contains("Bronze"))sum=sum+1;
        if (endurance.contains("Silber"))sum=sum+2;
        if (endurance.contains("Gold"))sum=sum+3;

        if (strength.contains("Bronze"))sum=sum+1;
        if (strength.contains("Silber"))sum=sum+2;
        if (strength.contains("Gold"))sum=sum+3;

        if (agility.contains("Bronze"))sum=sum+1;
        if (agility.contains("Silber"))sum=sum+2;
        if (agility.contains("Gold"))sum=sum+3;

        if (coordination.contains("Bronze"))sum=sum+1;
        if (coordination.contains("Silber"))sum=sum+2;
        if (coordination.contains("Gold"))sum=sum+3;

        return sum;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dsa, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, Medal.class);
            startActivity(intent);
            this.finish();
            return true;
        }

        if(id==R.id.action_uploadDSA){

        }
            return super.onOptionsItemSelected(item);

        }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch(keycode) {
            case KeyEvent.KEYCODE_BACK:
                Intent intent = new Intent(this, Medal.class);
                startActivity(intent);
                return true;
        }

        return super.onKeyDown(keycode, e);
    }

    class DSATask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Show progress dialog during task execution
            pDialog = new ProgressDialog(DSA.this);
            pDialog.setMessage("Loading data..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            int success;

            try {
                UUID uuid = UUID.randomUUID();
                String id = uuid.toString();

                //Put login credentials in parameter list
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id", id));
                params.add(new BasicNameValuePair("id_athlete", athlete_id));
                params.add(new BasicNameValuePair("medal_endurance", medal_endurance));
                params.add(new BasicNameValuePair("medal_strength", medal_strength));
                params.add(new BasicNameValuePair("medal_agility", medal_agility));
                params.add(new BasicNameValuePair("medal_coordination", medal_coordination));
                params.add(new BasicNameValuePair("medal_total", total_medal));

                //Execute HTTP Request
                JSONObject json = jsonParser.makeHttpRequest(URL, "POST", params);

                Log.d("Post results..", json.toString());

                //Get success value of HTTP Response
                success = json.getInt(RESPONSE_SUCCESS);

                //Return HTTP Response message to AsyncTask to use this in onPostExecute method
                if (success == 1) {
                    Log.d("Results posted", json.toString());

                    return "Auswertung hochgeladen";


                } else {
                    Log.d("Failed posting results", json.toString());

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

            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

        }
    }
}

