package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewSports extends AppCompatActivity {

    DatabaseHelperSports myDbSp;
    DatabaseHelperStation myDbSt;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    EditText sports_name;

    String lat, lng;

    int[] buffer = new int[100];
    ArrayList<String> sports = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sports);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        for(int x=0;x<buffer.length;x++) buffer[x]=0;


        myDbSp = new DatabaseHelperSports(this);
        myDbSt = new DatabaseHelperStation(this);

        Intent intent = getIntent();
        lat = intent.getStringExtra("lat");
        lng = intent.getStringExtra("lng");


        sports_name = (EditText) findViewById(R.id.editText_sportstation_name);


        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp_Sports);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(NewSports.this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                int gP= groupPosition;
                int cP= childPosition;
                String selected_sports = Integer.toString(gP)+"-"+Integer.toString(cP);


                if(!sports.contains(selected_sports)){
                    sports.add(selected_sports);
                    Toast.makeText(NewSports.this, selected_sports, Toast.LENGTH_LONG).show();
                }
                else if(sports.contains(selected_sports)){
                    sports.remove(selected_sports);
                    Toast.makeText(NewSports.this, selected_sports, Toast.LENGTH_LONG).show();
                }



//                if(buffer[index]==0){
//                    v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//                    buffer[index]=1;
//                }
//                else if(buffer[index]==1) {
//                    v.setBackgroundColor(Color.TRANSPARENT);
//                    buffer[index]=0;
//                }


                if (groupPosition == 0) {


                }

                else if (groupPosition == 1) {


                }

                else  if (groupPosition == 2) {


                }

               else if (groupPosition == 3) {


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
        ArrayList<String> endurance_List = getData("0");
        ArrayList<String> strength_List = getData("1");
        ArrayList<String> agility_List = getData("2");
        ArrayList<String> coordination_List = getData("3");

        listDataChild.put(listDataHeader.get(0), endurance_List);
        listDataChild.put(listDataHeader.get(1), strength_List);
        listDataChild.put(listDataHeader.get(2), agility_List);
        listDataChild.put(listDataHeader.get(3), coordination_List);

    }

    public ArrayList<String> getData(String id_sport_category) {
        ArrayList<String> SportList = new ArrayList<String>();

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
        for (int i=0; i<mTestArray.length; i++) SportList.add(mTestArray[i]);

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
                String category="";
                String sport_id="";
                String unit="";
                StringBuilder strBuilder  = new StringBuilder();
                for(String s: sports){

                    String[] splitResult=s.split("-");
                    category=splitResult[0];
                    sport_id=splitResult[1];



                    if(category.equals("0")) {
                        unit="min";
                        }
                    if(category.equals("1")) {
                        unit="m";
                    }
                    if(category.equals("2")) {
                        unit="sek";
                    }
                    if(category.equals("3")) {
                        unit="m";
                }

                 boolean isInserted_sp = myDbSp.insertData(category, sport_id, unit, lat,lng);

                    if (isInserted_sp == true) {
                        Cursor res = myDbSp.getAllData();
                        res.moveToLast();
                        strBuilder.append(res.getString(0));
                        strBuilder.append(",");
                    }
                    if(isInserted_sp == false){

                    }
                }

                strBuilder.deleteCharAt(strBuilder.length()-1);
                String station_sport = strBuilder.toString();

                boolean isInserted_st = myDbSt.insertData(sports_name.getText().toString(), lat, lng, station_sport);
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