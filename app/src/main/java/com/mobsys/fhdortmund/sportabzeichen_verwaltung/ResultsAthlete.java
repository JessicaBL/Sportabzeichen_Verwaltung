package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

        if(res_Results.getCount()==0) {
            Result_List.add("Keine Ergebnisse vorhanden");
        }
        else {
            while (res_Results.moveToNext()) {

                String id_sports = res_Results.getString(2);
                String result = res_Results.getString(3);
                String result_nr = res_Results.getString(4);
                String id_pruefer = res_Results.getString(5);

                res_Athlete.moveToFirst();
                name = res_Athlete.getString(1);
                surname = res_Athlete.getString(2);
                birthday = res_Athlete.getString(3);
                sex = res_Athlete.getString(4);



                Cursor res_Sports = myDbSp.selectSingleData(id_sports);
                res_Sports.moveToFirst();
                String category = res_Sports.getString(1);
                String sports = res_Sports.getString(2);
                String unit = res_Sports.getString(3);


                Cursor res_Pruefer = myDbPr.getActive();
                res_Pruefer.moveToFirst();
                String cur_pruefer_id = res_Pruefer.getString(0);

                String[] mTestArray = new String[0];

                if (category.equals("0")) {
                    mTestArray = getResources().getStringArray(R.array.endurance_array);
                }
                if (category.equals("1")) {
                    mTestArray = getResources().getStringArray(R.array.strength_array);
                }
                if (category.equals("2")) {
                    mTestArray = getResources().getStringArray(R.array.agility_array);
                }
                if (category.equals("3")) {
                    mTestArray = getResources().getStringArray(R.array.coordination_array);
                }


                String sport_name_string = mTestArray[Integer.parseInt(sports)];

                if (cur_pruefer_id.equals(id_pruefer)) {
                    Result_List.add(sport_name_string + ": " + result + " " + unit + ", Datum: " + result_nr);
                } else {
                    Result_List.add(sport_name_string + " : Ergebnis für Prüfer unsichtbar, Datum: " + result_nr);

                }
            }
        }

        ListAdapter adapter = new ArrayAdapter(ResultsAthlete.this, android.R.layout.simple_list_item_1,Result_List);

        final ListView lv = (ListView)findViewById(R.id.listView_results_athletes);
        lv.setAdapter(adapter);
    }
}
