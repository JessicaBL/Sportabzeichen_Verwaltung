package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ResultsAthlete extends AppCompatActivity {

    DatabaseHelper myDb;
    DatabaseHelperSports myDbSp;
    DatabaseHelperResults myDbRs;
    DatabaseHelperPruefer myDbPr;
    String id_athlete;
    ArrayList<String> Result_List= new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_athlete);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDb = new DatabaseHelper(this);
        myDbSp = new DatabaseHelperSports(this);
        myDbRs = new DatabaseHelperResults(this);
        myDbPr = new DatabaseHelperPruefer(this);

        populateListView();
    }

    public void populateListView() {
        Intent intent = getIntent();
        id_athlete =intent.getStringExtra("id_athlete");

        Cursor res_Athlete = myDb.selectSingleDataAthlete(id_athlete);

        res_Athlete.moveToFirst();
        String name = res_Athlete.getString(1);
        String surname = res_Athlete.getString(2);
        String birthday = res_Athlete.getString(3);
        String sex = res_Athlete.getString(4);

        getSupportActionBar().setTitle(name+" " + surname+", "+birthday+", "+sex);
        getSupportActionBar().setSubtitle("Ergebnisse");

        Cursor res_Results = myDbRs.selectSingleDataAthlete(id_athlete);
        if(res_Results.getCount()==0) {
            Result_List.add("Keine Ergebnisse vorhanden");
        }
        else {
            while (res_Results.moveToNext()) {

                String id_sports = res_Results.getString(2);
                String result = res_Results.getString(3);
                String result_nr = res_Results.getString(4);
                String id_pruefer = res_Results.getString(5);

                Cursor res_Sports = myDbSp.selectSingleData(id_sports);
                res_Sports.moveToFirst();
                String category = res_Sports.getString(1);
                String sports = res_Sports.getString(2);
                String unit = res_Sports.getString(3);


                Cursor res_Pruefer = myDbPr.getActive();
                res_Pruefer.moveToFirst();
                String cur_pruefer_id = res_Pruefer.getString(0);



                if (cur_pruefer_id.equals(id_pruefer)) {
                    Result_List.add(sports + ": " + result + " " + unit + ", Datum: " + result_nr);
                } else {
                    Result_List.add(sports + " : Ergebnis für Prüfer unsichtbar, Datum: " + result_nr);
                }
            }
        }

        ListAdapter adapter = new ArrayAdapter(ResultsAthlete.this, android.R.layout.simple_list_item_1,Result_List);

        final ListView lv = (ListView)findViewById(R.id.listView_results_athletes);
        lv.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id==android.R.id.home){

            Intent intent = new Intent(this, AthleteOverview.class);
            startActivity(intent);
            this.finish();
            return true;

        }



        return super.onOptionsItemSelected(item);


    }
}
