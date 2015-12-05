package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Medal_ListView extends AppCompatActivity {

    DatabaseHelper myDb;
    DatabaseHelperSports myDbSp;
    DatabaseHelperResults myDbRs;
    DatabaseHelperPruefer myDbPr;


    String id_sport_category="";
    String athlete_id="";
    String result="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medal__list_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDb = new DatabaseHelper(this);
        myDbSp = new DatabaseHelperSports(this);
        myDbRs = new DatabaseHelperResults(this);
        myDbPr = new DatabaseHelperPruefer(this);

        Intent intent = getIntent();
        id_sport_category = intent.getStringExtra("category");
        athlete_id = intent.getStringExtra("id_athlete");

        Populate_ListView();
    }

    public void Populate_ListView() {
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
                SportList.add(sport_name_string + ": " + result + " " + sport_unit + ", " + result_date);
            } else if (id_sport_category.equals(sports_category)) {
                SportList.add(sport_name_string + ": Ergebnis für Prüfer unsichtbar, " + result_date);
            }


        }
        if (SportList.isEmpty()) {
            SportList.add("Keine Ergebnisse vorhanden");

        }
        ListAdapter adapter = new ArrayAdapter<>(Medal_ListView.this, android.R.layout.simple_list_item_1,SportList);

        final ListView lv = (ListView)findViewById(R.id.listView_category);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                result = String.valueOf(position);
            }


        });

}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_medal_listview, menu);
        return true;
    }
     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         // Handle action bar item clicks here. The action bar will
         // automatically handle clicks on the Home/Up button, so long
         // as you specify a parent activity in AndroidManifest.xml.
         int id = item.getItemId();

         if (id == R.id.action_add_category) {

             if(result.equals("")){
                 Toast.makeText(Medal_ListView.this, "Bitte Ergebnis auswählen", Toast.LENGTH_LONG).show();
             }
             if(result.equals("Keine Ergebnisse vorhanden")){
                 Toast.makeText(Medal_ListView.this, "Keine Ergebnisse vorhanden", Toast.LENGTH_LONG).show();
             }

             else{
                 Intent intent = new Intent(this, Medal.class);
                 if(id_sport_category.equals("0")) intent.putExtra("result_endurance", result);
                 startActivity(intent);
             }
         }

             if (id == android.R.id.home) {

             Intent intent = new Intent(this, Medal.class);
             startActivity(intent);
             this.finish();
             return true;

         }
         return super.onOptionsItemSelected(item);
     }

 }
