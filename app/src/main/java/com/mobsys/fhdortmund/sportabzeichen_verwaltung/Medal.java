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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class Medal extends AppCompatActivity {

    private ProgressDialog pDialog;

    DatabaseHelper myDb;
    DatabaseHelperSports myDbSp;
    DatabaseHelperResults myDbRs;
    DatabaseHelperCondition myDbCon;

    JSONParser jsonParser = new JSONParser();
    SessionManager session;

    Spinner spinner_athlete;
    String athlete_id = "";

    String endurance = "", strength = "", agility = "", coordination = "";

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    String ip_port;
    String URL;

    private static final String RESPONSE_SUCCESS = "success";
    private static final String RESPONSE_GOALRESULTS = "goalresults";
    private static final String RESPONSE_GOALRESULT = "goalresult";
    private static final String RESPONSE_MEDAL = "medal";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDb = new DatabaseHelper(this);
        myDbSp = new DatabaseHelperSports(this);
        myDbRs = new DatabaseHelperResults(this);
        myDbCon = new DatabaseHelperCondition(this);

        session = new SessionManager(getApplicationContext());

        ip_port = getApplicationContext().getString(R.string.ip_port);
        URL = "http://" + ip_port + "/sportabzeichen/dsa.php";

        ArrayList<String> AthletesList = populateSpinner();

        spinner_athlete = (Spinner) findViewById(R.id.spinner_athlete_medal);
        ArrayAdapter<String> adapter_athletes = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                AthletesList);

        adapter_athletes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_athlete.setAdapter(adapter_athletes);

        spinner_athlete.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = String.valueOf(spinner_athlete.getSelectedItem());
                final String[] split_Result = item.split(" ");
                athlete_id = split_Result[0];

                ImageView img_en = (ImageView) findViewById(R.id.imageView_endurance);
                ImageView img_str = (ImageView) findViewById(R.id.imageView_strength);
                ImageView img_ag = (ImageView) findViewById(R.id.imageView_agility);
                ImageView img_co = (ImageView) findViewById(R.id.imageView_coordination);

                img_en.setImageResource(R.drawable.icon_white);
                img_str.setImageResource(R.drawable.icon_white);
                img_ag.setImageResource(R.drawable.icon_white);
                img_co.setImageResource(R.drawable.icon_white);

                endurance = "";
                strength = "";
                agility = "";
                coordination = "";

                // get the listview
                expListView = (ExpandableListView) findViewById(R.id.lvExp);

                // preparing list data -> ASYNCTASK
                MedalTask medalTask = new MedalTask();
                medalTask.execute();

                listAdapter = new ExpandableListAdapter(Medal.this, listDataHeader, listDataChild);

                // setting list adapter
                expListView.setAdapter(listAdapter);

                // Listview on child click listener
                expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v,
                                                int groupPosition, int childPosition, long id) {

                        ImageView img_en = (ImageView) findViewById(R.id.imageView_endurance);
                        ImageView img_str = (ImageView) findViewById(R.id.imageView_strength);
                        ImageView img_ag = (ImageView) findViewById(R.id.imageView_agility);
                        ImageView img_co = (ImageView) findViewById(R.id.imageView_coordination);

                        if (groupPosition == 0) {
                            img_en.setImageResource(R.drawable.icon_white);
                            endurance = listDataHeader.get(groupPosition)
                                    + " - "
                                    + listDataChild.get(
                                    listDataHeader.get(groupPosition)).get(
                                    childPosition);

                            if (endurance.contains("Keine")) {
                                Toast.makeText(Medal.this, "Ergebnis nicht ausreichend", Toast.LENGTH_LONG).show();
                                endurance = "";
                            } else {
                                if (endurance.contains("Bronze"))
                                    img_en.setImageResource(R.drawable.icon_bronze);
                                if (endurance.contains("Silber"))
                                    img_en.setImageResource(R.drawable.icon_silver);
                                if (endurance.contains("Gold"))
                                    img_en.setImageResource(R.drawable.icon_gold);

                            }
                        }
                        if (groupPosition == 1) {
                            img_str.setImageResource(R.drawable.icon_white);
                            strength = listDataHeader.get(groupPosition)
                                    + " - "
                                    + listDataChild.get(
                                    listDataHeader.get(groupPosition)).get(
                                    childPosition);

                            if (strength.contains("Keine")) {
                                Toast.makeText(Medal.this, "Ergebnis nicht ausreichend", Toast.LENGTH_LONG).show();
                                strength = "";
                            } else {
                                if (strength.contains("Bronze"))
                                    img_str.setImageResource(R.drawable.icon_bronze);
                                if (strength.contains("Silber"))
                                    img_str.setImageResource(R.drawable.icon_silver);
                                if (strength.contains("Gold"))
                                    img_str.setImageResource(R.drawable.icon_gold);
                            }
                        }
                        if (groupPosition == 2) {
                            img_ag.setImageResource(R.drawable.icon_white);
                            agility = listDataHeader.get(groupPosition)
                                    + " - "
                                    + listDataChild.get(
                                    listDataHeader.get(groupPosition)).get(
                                    childPosition);

                            if (agility.contains("Keine")) {
                                Toast.makeText(Medal.this, "Ergebnis nicht ausreichend", Toast.LENGTH_LONG).show();
                                agility = "";
                            } else {
                                if (agility.contains("Bronze"))
                                    img_ag.setImageResource(R.drawable.icon_bronze);
                                if (agility.contains("Silber"))
                                    img_ag.setImageResource(R.drawable.icon_silver);
                                if (agility.contains("Gold"))
                                    img_ag.setImageResource(R.drawable.icon_gold);
                            }
                        }
                        if (groupPosition == 3) {
                            img_co.setImageResource(R.drawable.icon_white);
                            coordination = listDataHeader.get(groupPosition)
                                    + " - "
                                    + listDataChild.get(
                                    listDataHeader.get(groupPosition)).get(
                                    childPosition);

                            if (coordination.contains("Keine")) {
                                Toast.makeText(Medal.this, "Ergebnis nicht ausreichend", Toast.LENGTH_LONG).show();
                                coordination = "";
                            } else {
                                if (coordination.contains("Bronze"))
                                    img_co.setImageResource(R.drawable.icon_bronze);
                                if (coordination.contains("Silber"))
                                    img_co.setImageResource(R.drawable.icon_silver);
                                if (coordination.contains("Gold"))
                                    img_co.setImageResource(R.drawable.icon_gold);
                            }
                        }

                        return false;

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        MedalTask medalTask = new MedalTask();
        medalTask.execute();

    }

    public ArrayList<String> populateSpinner() {
        Cursor res = myDb.getAllData();

        ArrayList<String> AthletesList = new ArrayList<String>();

        while (res.moveToNext()) {
            String ID = res.getString(0);
            String name = res.getString(1);
            String surname = res.getString(2);
            AthletesList.add(ID + " - " + name + " " + surname);
        }

        return AthletesList;
    }

    public void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Ausdauer");
        listDataHeader.add("Kraft");
        listDataHeader.add("Schnelligkeit");
        listDataHeader.add("Koordination");

        //Get Data
        ArrayList<String> endurance_List = getData("Ausdauer", athlete_id);
        ArrayList<String> strength_List = getData("Kraft", athlete_id);
        ArrayList<String> agility_List = getData("Schnelligkeit", athlete_id);
        ArrayList<String> coordination_List = getData("Koordination", athlete_id);

        listDataChild.put(listDataHeader.get(0), endurance_List);
        listDataChild.put(listDataHeader.get(1), strength_List);
        listDataChild.put(listDataHeader.get(2), agility_List);
        listDataChild.put(listDataHeader.get(3), coordination_List);

    }

    public ArrayList<String> getData(String sport_category, String athlete_id) {
        Cursor res = myDbRs.selectSingleDataAthlete(athlete_id);

        ArrayList<String> SportList = new ArrayList<String>();

        while (res.moveToNext()) {

            String result_id_pruefer = res.getString(1);
            String id_sports_result_db = res.getString(3);
            String result = res.getString(4);
            String result_date = res.getString(5);

            String current_pruefer_id = session.getUserId();

            Cursor res_sports = myDbSp.selectSingleCategory(sport_category);

            while (res_sports.moveToNext()) {

                String id_sports = res_sports.getString(0);
                String sport_name_string = res_sports.getString(2);
                String sport_unit = res_sports.getString(3);

                if (current_pruefer_id.equals(result_id_pruefer) && id_sports_result_db.equals(id_sports)) {

                    Cursor res_ath = myDb.selectSingleDataAthlete(athlete_id);
                    res_ath.moveToFirst();
                    String sex = res_ath.getString(3);
                    String birthday = res_ath.getString(4);


                    //Compare result with target
                    String medal = compareResult(birthday, sex, sport_category, sport_name_string, sport_unit, result);

                    SportList.add(sport_name_string + ": " + result + " " + sport_unit + ", " + result_date + ", " + medal);
                } else if (id_sports_result_db.equals(id_sports)) {
                    SportList.add(sport_name_string + ": Ergebnis für Prüfer unsichtbar, " + result_date);
                }
            }
        }
        if (SportList.isEmpty()) {
            SportList.add("Keine Ergebnisse vorhanden");
        }
        return SportList;
    }

    public String compareResult(String birthday, String sex, String sports_category, String sports_name, String sports_unit, String result) {

        String sportsId;
        String conId;
        String earnedMedal = "";
        double result_double = Double.parseDouble(result);
        double bronze = 0;
        double silver = 0;
        double gold = 0;


        Cursor resSp = myDbSp.selectSingleSports(sports_name);

        if (resSp.getCount() > 0) {

            resSp.moveToFirst();
            sportsId = resSp.getString(0);

            String birthday_split[] = birthday.split(" ");
            int birthyear = Integer.parseInt(birthday_split[2]);


            Calendar c = Calendar.getInstance();
            int cur_year = c.get(Calendar.YEAR);
            int age = cur_year - birthyear;

            Cursor resCon = myDbCon.selectSingleData(age, sex);

            if (resCon.getCount() > 0) {

                resCon.moveToFirst();
                conId = resCon.getString(0);

                int success;

                try {

                    JSONArray goalresults;

                    //Put result parameters in list
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("id_sports", sportsId));
                    params.add(new BasicNameValuePair("id_con", conId));

                    //Execute HTTP Request
                    JSONObject json = jsonParser.makeHttpRequest(URL, "GET", params);

                    Log.d("Populating data", "Load goal results...");

                    //Get success value of HTTP Response
                    success = json.getInt(RESPONSE_SUCCESS);

                    //Return HTTP Response message to AsyncTask to use this in onPostExecute method
                    if (success == 1) {
                        Log.d("Laoding data successful", json.toString());

                        String goalresult = null;
                        String medal = null;

                        try {

                            goalresults = json.getJSONArray(RESPONSE_GOALRESULTS);

                            for (int i = 0; i < goalresults.length(); i++) {

                                JSONObject res = goalresults.getJSONObject(i);

                                goalresult = res.getString(RESPONSE_GOALRESULT);
                                medal = res.getString(RESPONSE_MEDAL);

                                if (medal.equals("bronze")) {
                                    bronze = Double.parseDouble(goalresult);
                                } else if (medal.equals("silber")) {
                                    silver = Double.parseDouble(goalresult);
                                } else if (medal.equals("gold")) {
                                    gold = Double.parseDouble(goalresult);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Ergebnisse müssen kleiner sein als Grenze (-->Zeit)
                        if (sports_category.equals("Ausdauer") || sports_category.equals("Schnelligkeit")) {
                            if (result_double > bronze) earnedMedal = "Keine Medaille";
                            if ((result_double <= bronze) && (result_double > silver))
                                earnedMedal = "Bronze";
                            if ((result_double <= silver) && (result_double > gold))
                                earnedMedal = "Silber";
                            if ((result_double <= gold)) earnedMedal = "Gold";
                        }
                        //Ergebnisse müssen größer sein als Grenze (-->Weite)
                        if (sports_category.equals("Kraft") || sports_category.equals("Koordination")) {
                            if (result_double < bronze) earnedMedal = "Keine Medaille";
                            if ((result_double >= bronze) && (result_double < silver))
                                earnedMedal = "Bronze";
                            if ((result_double >= silver) && (result_double < gold))
                                earnedMedal = "Silber";
                            if ((result_double >= gold)) earnedMedal = "Gold";
                        }

                        return earnedMedal;

                    } else {
                        Log.d("Loading data failed!", json.toString());
                        earnedMedal = "Keine";
                        return earnedMedal;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                earnedMedal = "Keine";
                return earnedMedal;

            } else {
                Log.d("Populating data", "Condition cannot be found in db");
                earnedMedal = "Keine";
                return earnedMedal;
            }
        } else {
            Log.d("Populating data", "Sport cannot be found in db");
            earnedMedal = "Keine";
            return earnedMedal;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_medal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_saveDSA) {
            if (endurance.equals("") || strength.equals("") || agility.equals("") || coordination.equals((""))) {
                Toast.makeText(Medal.this, "Bitte jede Kategorie eintragen", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(this, DSA.class);
                intent.putExtra("endurance", endurance);
                intent.putExtra("strength", strength);
                intent.putExtra("agility", agility);
                intent.putExtra("coordination", coordination);
                intent.putExtra("athlete", athlete_id);

                startActivity(intent);
            }

        }
        if (id == android.R.id.home) {

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch (keycode) {
            case KeyEvent.KEYCODE_BACK:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onKeyDown(keycode, e);
    }

    class MedalTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Show progress dialog during task execution
            pDialog = new ProgressDialog(Medal.this);
            pDialog.setMessage("Loading data..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            prepareListData();
            return "done";
        }


        @Override
        protected void onPostExecute(String response) {

            //Close progress dialog
            pDialog.dismiss();

            Toast.makeText(getApplicationContext(), "Medaillen aktualisiert", Toast.LENGTH_LONG).show();

        }
    }

}