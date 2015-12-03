package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

        myDb = new DatabaseHelper(this);
        myDbSp = new DatabaseHelperSports(this);
        myDbRs = new DatabaseHelperResults(this);
        myDbPr = new DatabaseHelperPruefer(this);



        populateListView();


    }

    private void populateListView() {

        Intent intent = getIntent();
        id_athlete =intent.getStringExtra("id_athlete");

        Cursor res_Results = myDbRs.selectSingleDataAthlete(id_athlete);
        Cursor res_Athlete = myDb.selectSingleDataAthlete(id_athlete);

        String name=null;
        String surname=null;
        String birthday=null;
        String sex=null;

        while (res_Results.moveToNext()){

            String id_sports=res_Results.getString(2);
            String result=res_Results.getString(3);
            String result_nr=res_Results.getString(4);
            String id_pruefer = res_Results.getString(5);

            res_Athlete.moveToFirst();
            name = res_Athlete.getString(1);
            surname = res_Athlete.getString(2);
            birthday = res_Athlete.getString(3);
            sex = res_Athlete.getString(4);

            Cursor res_Sports  = myDbSp.selectSingleData(id_sports);
            res_Sports.moveToFirst();
            String sport_name = res_Sports.getString(1);
            String sport_parameter = res_Sports.getString(2);
            String sport_unit = res_Sports.getString(3);

            Cursor res_Pruefer = myDbPr.getActive();
            res_Pruefer.moveToFirst();
            String cur_pruefer_id= res_Pruefer.getString(0);

            if (cur_pruefer_id.equals(id_pruefer)) {
                Result_List.add(sport_name + " " + sport_parameter + ": " + result + " " + sport_unit + ", Datum: " + result_nr);
            }
            else{
                Result_List.add("Ergebnis für Disziplin " +sport_name + " " + sport_parameter +  " für Prüfer unsichtbar, Datum: " + result_nr);

            }
            }

        TextView results_athletes=(TextView)findViewById(R.id.textView_results_athletes);
        results_athletes.setText("Ergebnisse für "+ name+" "+surname+", "+birthday+", "+sex);

        ListAdapter adapter = new ArrayAdapter(ResultsAthlete.this, android.R.layout.simple_list_item_1,Result_List);

        final ListView lv = (ListView)findViewById(R.id.listView_results_athletes);
        lv.setAdapter(adapter);
    }
}
