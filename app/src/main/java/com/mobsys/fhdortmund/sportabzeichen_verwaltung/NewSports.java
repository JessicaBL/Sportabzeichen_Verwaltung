package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class NewSports extends AppCompatActivity {

    DatabaseHelperSports myDbSp;
    DatabaseHelperStation myDbSt;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    EditText sports_name;

    String lat, lng;

    ArrayList<String> sports = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sports);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        myDbSp = new DatabaseHelperSports(this);
        myDbSt = new DatabaseHelperStation(this);

        //---------------------------------------------------
        //LÖSCHEN, WENN ONLINE-DISZIPLIN-VERWALTUNG INTEGRIERT
//        myDbSp.insertData("Ausdauer", "3.000 m Lauf", "Min.");
//        myDbSp.insertData("Ausdauer", "10 km Lauf", "Min.");
//        myDbSp.insertData("Ausdauer", "7,5 km Walking/Nordic Walking", "Min.");
//        myDbSp.insertData("Ausdauer", "Schwimmen", "Min.");
//
//        myDbSp.insertData("Kraft", "Medizinball (2 kg)", "m");
//        myDbSp.insertData("Kraft", "Kugelstoßen", "m");
//        myDbSp.insertData("Kraft", "Standweitsprung", "m");
//
//        myDbSp.insertData("Schnelligkeit", "Laufen", "Sek.");
//        myDbSp.insertData("Schnelligkeit", "25 m Schwimmen", "Sek.");
//        myDbSp.insertData("Schnelligkeit", "200 m Radfahren (fl. Start)", "m");
//
//        myDbSp.insertData("Koordination", "Hochsprung", "m");
//        myDbSp.insertData("Koordination", "Weitsprung", "m");
//        myDbSp.insertData("Koordination", "Schleuderball (1 kg)", "m");
        //----------------------------------------------------
        //----------------------------------------------------

        Intent intent = getIntent();
        lat = intent.getStringExtra("lat");
        lng = intent.getStringExtra("lng");


        sports_name = (EditText) findViewById(R.id.editText_sportstation_name);


        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp_Sports_change);
        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(NewSports.this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);


        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

               String category_name = parent.getExpandableListAdapter().getGroup(groupPosition).toString();
               String sports_name = parent.getExpandableListAdapter().getChild(groupPosition, childPosition).toString();
               Cursor res = myDbSp.selectIDFromCategorySports(category_name, sports_name);
               res.moveToFirst();
               String selected_sports = res.getString(0);

                if (!sports.contains(selected_sports)) {
                    sports.add(selected_sports);
                    v.setBackgroundColor(Color.RED);
                    v.setActivated(true);
                } else if (sports.contains(selected_sports)) {
                    sports.remove(selected_sports);
                    v.setBackgroundColor(Color.TRANSPARENT);
                    v.setActivated(false);
                }

                return false;

            }
        });

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
        ArrayList<String> endurance_List = getData("Ausdauer");
        ArrayList<String> strength_List = getData("Kraft");
        ArrayList<String> agility_List = getData("Schnelligkeit");
        ArrayList<String> coordination_List = getData("Koordination");

        listDataChild.put(listDataHeader.get(0), endurance_List);
        listDataChild.put(listDataHeader.get(1), strength_List);
        listDataChild.put(listDataHeader.get(2), agility_List);
        listDataChild.put(listDataHeader.get(3), coordination_List);

    }

    public ArrayList<String> getData(String sport_category) {
        ArrayList<String> SportList = new ArrayList<String>();

        Cursor res = myDbSp.selectSingleCategory(sport_category);

        while(res.moveToNext()){
            SportList.add(res.getString(2));
        }

        return SportList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_sports, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_sports) {

            if(sports_name.getText().toString().equals("")){
                Toast.makeText(NewSports.this, "Bitte Sportstation-Namen eingeben", Toast.LENGTH_LONG).show();
            }
            else if(sports.isEmpty()){
                Toast.makeText(NewSports.this, "Bitte mindestens eine Disziplin auswählen", Toast.LENGTH_LONG).show();
            }
            else{
                StringBuilder strBuilder  = new StringBuilder();

                for(String s: sports){
                        strBuilder.append(s);
                        strBuilder.append(",");
                    }

                strBuilder.deleteCharAt(strBuilder.length()-1);
                String station_sport = strBuilder.toString();

                UUID uuid = UUID.randomUUID();
                String station_id = uuid.toString();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = new Date();
                String current_timestamp = dateFormat.format(date);

                boolean isInserted_st = myDbSt.insertData(station_id, sports_name.getText().toString(), lat, lng, station_sport, current_timestamp, "0");
                if (isInserted_st == true) {
                    Toast.makeText(NewSports.this, "Sportstation angelegt", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(NewSports.this, Maps.class);
                    startActivity(intent);
                }
                else
                    Toast.makeText(NewSports.this, "Sportstation nicht angelegt", Toast.LENGTH_LONG).show();
            }
            }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch(keycode) {
            case KeyEvent.KEYCODE_BACK:
                Intent intent = new Intent(this, Maps.class);
                startActivity(intent);
                return true;
        }

        return super.onKeyDown(keycode, e);
    }

    }