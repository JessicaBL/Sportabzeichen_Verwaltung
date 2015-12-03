package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ShowResults extends AppCompatActivity {

    DatabaseHelper myDb;
    DatabaseHelperSports myDbSp;
    DatabaseHelperResults myDbRs;
    DatabaseHelperPruefer myDbPr;

    String id_sports_results;
    ArrayList<String> Result_List= new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_results);

        myDb = new DatabaseHelper(this);
        myDbSp = new DatabaseHelperSports(this);
        myDbRs = new DatabaseHelperResults(this);
        myDbPr = new DatabaseHelperPruefer(this);

        populateListView();

    }

    //ListView mit SQLite-Daten erstellen
    public void populateListView(){

        Intent intent = getIntent();
        id_sports_results =intent.getStringExtra("id");

        Cursor res_Results = myDbRs.selectSingleDataSports(id_sports_results);


        String sport_name = null;
        String sport_parameter = null;

        while(res_Results.moveToNext()){

          String id_athlete = res_Results.getString(1);
          String id_sports = res_Results.getString(2);
          String result = res_Results.getString(3);
          String result_nr = res_Results.getString(4);
          String id_pruefer = res_Results.getString(5);

          Cursor res_Athlete = myDb.selectSingleDataAthlete(id_athlete);
          res_Athlete.moveToFirst();
          String name = res_Athlete.getString(1);
          String surname = res_Athlete.getString(2);

          Cursor res_Sports = myDbSp.selectSingleData(id_sports);
          res_Sports.moveToFirst();
          sport_name = res_Sports.getString(1);
          sport_parameter = res_Sports.getString(2);
          String sport_unit = res_Sports.getString(3);

          Cursor res_Pruefer = myDbPr.getActive();
          res_Pruefer.moveToFirst();
          String cur_pruefer_id= res_Pruefer.getString(0);

          if (cur_pruefer_id.equals(id_pruefer)) {
                Result_List.add(name + " " + surname + ": " + result + " " + sport_unit + ", Datum: " + result_nr);
            }
          else{
              Result_List.add("Ergebnis für " +name + " " + surname + " für Prüfer unsichtbar, Datum: " + result_nr);
          }

        }

        TextView results_all=(TextView)findViewById(R.id.textView_Results_all);
        results_all.setText("Ergebnisse für Disziplin "+ sport_name+" "+sport_parameter);


        ListAdapter adapter = new ArrayAdapter(ShowResults.this, android.R.layout.simple_list_item_1,Result_List);

        final ListView lv = (ListView)findViewById(R.id.listView_results_All);
        lv.setAdapter(adapter);

    }

}
