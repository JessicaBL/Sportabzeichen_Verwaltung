package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.Intent;
import android.database.Cursor;
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

public class ChangeStation extends AppCompatActivity {

    DatabaseHelperSports myDbSp;
    DatabaseHelperStation myDbSt;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    EditText sports_name;

    String id_station;
    ArrayList<String> sports = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_station);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDbSp = new DatabaseHelperSports(this);
        myDbSt = new DatabaseHelperStation(this);

        Intent intent = getIntent();
        id_station = intent.getStringExtra("id_station");

        Cursor cursor_st=myDbSt.selectSingleData(id_station);
        cursor_st.moveToFirst();
        String name=cursor_st.getString(1);
        getSupportActionBar().setTitle("Station "+name+" bearbeiten");

        sports_name = (EditText) findViewById(R.id.editText_sportstation_name);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp_Sports_change);
        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(ChangeStation.this, listDataHeader, listDataChild);

        expListView.setAdapter(listAdapter);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
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
                    v.setActivated(true);
                } else if (sports.contains(selected_sports)) {
                    sports.remove(selected_sports);
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
                Toast.makeText(ChangeStation.this, "Bitte Sportstation-Namen eingeben", Toast.LENGTH_LONG).show();
            }
            else if(sports.isEmpty()){
                Toast.makeText(ChangeStation.this, "Bitte mindestens eine Disziplin auswählen", Toast.LENGTH_LONG).show();
            }
            else{
                StringBuilder strBuilder  = new StringBuilder();

                for(String s: sports){
                    strBuilder.append(s);
                    strBuilder.append(",");
                }

                strBuilder.deleteCharAt(strBuilder.length()-1);
                String station_sport = strBuilder.toString();

                Cursor res = myDbSt.selectSingleData(id_station);
                res.moveToFirst();
                String lat=res.getString(2);
                String lng=res.getString(3);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = new Date();
                String current_timestamp = dateFormat.format(date);

                boolean change_st=myDbSt.updateData(id_station, sports_name.getText().toString(), lat, lng, station_sport, current_timestamp, "0");

                if (change_st == true) {
                    Toast.makeText(ChangeStation.this, "Sportstation geändert", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ChangeStation.this, Maps.class);
                    startActivity(intent);
                }
                else
                    Toast.makeText(ChangeStation.this, "Sportstation nicht geändert", Toast.LENGTH_LONG).show();
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
