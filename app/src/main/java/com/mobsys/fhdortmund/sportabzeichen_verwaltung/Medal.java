package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Medal extends AppCompatActivity {


    DatabaseHelper myDb;
    DatabaseHelperSports myDbSp;
    DatabaseHelperResults myDbRs;
    DatabaseHelperPruefer myDbPr;

    Spinner spinner_athlete;
    String athlete_id="";

    String endurance="", strength="", agility="", coordination="";

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

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
        myDbPr = new DatabaseHelperPruefer(this);

        ArrayList<String> AthletesList = populateSpinner();

        spinner_athlete = (Spinner) findViewById(R.id.spinner_athlete_medal);
        ArrayAdapter<String> adapter_athletes = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                AthletesList);

        adapter_athletes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_athlete.setAdapter(adapter_athletes);

        CheckBox checkBox_endurance = (CheckBox) findViewById(R.id.checkBox_endurance);
        CheckBox checkBox_strength = (CheckBox) findViewById(R.id.checkBox_strength);
        CheckBox checkBox_agility = (CheckBox) findViewById(R.id.checkBox_strength_agility);
        CheckBox checkBox_coordination = (CheckBox) findViewById(R.id.checkBox_coordination);

        checkBox_endurance.setClickable(false);
        checkBox_strength.setClickable(false);
        checkBox_agility.setClickable(false);
        checkBox_coordination.setClickable(false);

        spinner_athlete.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = String.valueOf(spinner_athlete.getSelectedItem());
                final String[] split_Result = item.split(" ");
                athlete_id = split_Result[0];

                // get the listview
                expListView = (ExpandableListView) findViewById(R.id.lvExp);

                // preparing list data
                prepareListData();

                listAdapter = new ExpandableListAdapter(Medal.this, listDataHeader, listDataChild);

                // setting list adapter
                expListView.setAdapter(listAdapter);

                // Listview on child click listener
                expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v,
                                                int groupPosition, int childPosition, long id) {

                        CheckBox checkBox_endurance = (CheckBox) findViewById(R.id.checkBox_endurance);
                        CheckBox checkBox_strength = (CheckBox) findViewById(R.id.checkBox_strength);
                        CheckBox checkBox_agility = (CheckBox) findViewById(R.id.checkBox_strength_agility);
                        CheckBox checkBox_coordination = (CheckBox) findViewById(R.id.checkBox_coordination);

                        checkBox_endurance.setClickable(false);
                        checkBox_strength.setClickable(false);
                        checkBox_agility.setClickable(false);
                        checkBox_coordination.setClickable(false);

                        if(groupPosition==0){
                            checkBox_endurance.setChecked(false);
                            endurance = listDataHeader.get(groupPosition)
                                        + " : "
                                        + listDataChild.get(
                                        listDataHeader.get(groupPosition)).get(
                                        childPosition);

                            if(endurance.contains("Keine")) {
                                Toast.makeText(Medal.this, "Ergebnis nicht ausreichend", Toast.LENGTH_LONG).show();
                                endurance="";
                            }
                            else{
                                checkBox_endurance.setChecked(true);
                            }
                        }
                        if(groupPosition==1){
                            checkBox_strength.setChecked(false);
                            strength = listDataHeader.get(groupPosition)
                                    + " : "
                                    + listDataChild.get(
                                    listDataHeader.get(groupPosition)).get(
                                    childPosition);

                            if(strength.contains("Keine")) {
                                Toast.makeText(Medal.this, "Ergebnis nicht ausreichend", Toast.LENGTH_LONG).show();
                                strength="";
                            }
                            else{
                                checkBox_strength.setChecked(true);
                            }                        }
                        if(groupPosition==2){
                            checkBox_agility.setChecked(false);
                            agility = listDataHeader.get(groupPosition)
                                    + " : "
                                    + listDataChild.get(
                                    listDataHeader.get(groupPosition)).get(
                                    childPosition);

                            if(agility.contains("Keine")) {
                                Toast.makeText(Medal.this, "Ergebnis nicht ausreichend", Toast.LENGTH_LONG).show();
                                agility="";
                            }
                            else{
                                checkBox_agility.setChecked(true);
                            }                        }
                        if(groupPosition==3){
                            checkBox_coordination.setChecked(false);
                            coordination = listDataHeader.get(groupPosition)
                                    + " : "
                                    + listDataChild.get(
                                    listDataHeader.get(groupPosition)).get(
                                    childPosition);

                            if(coordination.contains("Keine")) {
                                Toast.makeText(Medal.this, "Ergebnis nicht ausreichend", Toast.LENGTH_LONG).show();
                                coordination="";
                            }
                            else{
                                checkBox_coordination.setChecked(true);
                            }                        }

                        return false;

                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        ArrayList<String> endurance_List = getData("0", athlete_id);
        ArrayList<String> strength_List = getData("1", athlete_id);
        ArrayList<String> agility_List = getData("2", athlete_id);
        ArrayList<String> coordination_List = getData("3", athlete_id);

        listDataChild.put(listDataHeader.get(0), endurance_List);
        listDataChild.put(listDataHeader.get(1), strength_List);
        listDataChild.put(listDataHeader.get(2), agility_List);
        listDataChild.put(listDataHeader.get(3), coordination_List);

    }

    public ArrayList<String> getData(String id_sport_category, String athlete_id) {
        Cursor res = myDbRs.selectSingleDataAthlete(athlete_id);

        ArrayList<String> SportList = new ArrayList<String>();

        while (res.moveToNext()) {

            String id_sports_result_db = res.getString(2);
            String result = res.getString(3);
            String result_date = res.getString(4);
            String result_id_pruefer = res.getString(5);

            Cursor res_sports = myDbSp.selectSingleData(id_sports_result_db);

            res_sports.moveToFirst();
            String sports_category = res_sports.getString(1);
            String sports = res_sports.getString(2);
            String sport_unit = res_sports.getString(3);

            String[] mTestArray = new String[0];

            if (id_sport_category.equals("0")) {
                mTestArray = getResources().getStringArray(R.array.endurance_array);
            }
            if (id_sport_category.equals("1")) {
                mTestArray = getResources().getStringArray(R.array.strength_array);
            }
            if (id_sport_category.equals("2")) {
                mTestArray = getResources().getStringArray(R.array.agility_array);
            }
            if (id_sport_category.equals("3")) {
                mTestArray = getResources().getStringArray(R.array.coordination_array);
            }
            String sport_name_string = mTestArray[Integer.parseInt(sports)];

            Cursor res_Pruefer = myDbPr.getActive();
            res_Pruefer.moveToFirst();
            String current_pruefer_id = res_Pruefer.getString(0);

            if (current_pruefer_id.equals(result_id_pruefer) && id_sport_category.equals(sports_category)) {

                Cursor res_ath = myDb.selectSingleDataAthlete(athlete_id);
                res_ath.moveToFirst();
                String birthday=res_ath.getString(3);
                String sex=res_ath.getString(4);


                //Compare result with target
                String medal = compareResult(birthday, sex, sports_category, sports, sport_unit, result);

                SportList.add(sport_name_string + ": " + result + " " + sport_unit + ", " + result_date+", "+medal);
            } else if (id_sport_category.equals(sports_category)) {
                SportList.add(sport_name_string + ": Ergebnis für Prüfer unsichtbar, " + result_date);
            }
        }
        if (SportList.isEmpty()) {
            SportList.add("Keine Ergebnisse vorhanden");
        }
        return SportList;
    }

    public String compareResult(String birthday, String sex, String sports_category, String sports, String sports_unit, String result) {

        double result_double = Double.parseDouble(result);
        double bronze;
        double silver;
        double gold;

        String medal="";

        String[] mTestArray = new String[0];

        if (sports_category.equals("0")) {
            mTestArray = getResources().getStringArray(R.array.m_1990_0_1);
        }
        if (sports_category.equals("1")) {
            mTestArray = getResources().getStringArray(R.array.m_1990_1_0);
        }
        if (sports_category.equals("2")) {
            mTestArray = getResources().getStringArray(R.array.m_1990_2_1);
        }
        if (sports_category.equals("3")) {
            mTestArray = getResources().getStringArray(R.array.m_1990_3_1);
        }
        bronze=Double.parseDouble(mTestArray[0]);
        silver=Double.parseDouble(mTestArray[1]);
        gold=Double.parseDouble(mTestArray[2]);

        //Ergebnisse müssen kleiner sein als Grenze (-->Zeit)
        if(sports_unit.equals("min")||sports_unit.equals("sek")||sports_unit.equals("h")){
            if(result_double>bronze) medal = "Keine Medaille";
            if((result_double<bronze)&&(result_double>silver)) medal="Bronze";
            if((result_double<silver)&&(result_double>gold)) medal="Silber";
            if((result_double<gold)) medal="Gold";
        }
        //Ergebnisse müssen größer sein als Grenze (-->Zeit)
        if(sports_unit.equals("m")){
            if(result_double<bronze) medal = "Keine Medaille";
            if((result_double>bronze)&&(result_double<silver)) medal="Bronze";
            if((result_double>silver)&&(result_double<gold)) medal="Silber";
            if((result_double>gold)) medal="Gold";
        }

        return medal;
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
            if(endurance.equals("")||strength.equals("")||agility.equals("")||coordination.equals((""))){
                Toast.makeText(Medal.this, "Bitte jede Kategorie eintragen", Toast.LENGTH_LONG).show();
            }
            else{
                Intent intent = new Intent(this, DSA.class);
                intent.putExtra("endurance",endurance);
                intent.putExtra("strength",strength);
                intent.putExtra("agility",agility);
                intent.putExtra("coordination",coordination);
                intent.putExtra("athlete",athlete_id);

                startActivity(intent);            }

        }
        if (id==android.R.id.home){

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch(keycode) {
            case KeyEvent.KEYCODE_BACK:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onKeyDown(keycode, e);
    }

}
