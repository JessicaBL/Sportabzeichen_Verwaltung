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
    String id_athlete;
    ArrayList<String> Result_List= new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_athlete);

        myDb = new DatabaseHelper(this);
        myDbSp = new DatabaseHelperSports(this);
        myDbRs = new DatabaseHelperResults(this);



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

            res_Athlete.moveToFirst();
            name = res_Athlete.getString(1);
            surname = res_Athlete.getString(2);
            birthday = res_Athlete.getString(3);
            sex = res_Athlete.getString(4);

            Cursor res_Sports  = myDbSp.selectSingleData(id_sports);
            res_Sports.moveToFirst();
            String sport_name = res_Sports.getString(1);
            String sport_info = res_Sports.getString(2);


            Result_List.add(sport_name +": "+ result+" "+sport_info+", Versuch-Nr: "+result_nr);
        }
        TextView results_athletes=(TextView)findViewById(R.id.textView_results_athletes);
        results_athletes.setText("Ergebnisse für "+ name+" "+surname+", "+birthday+", "+sex);

        ListAdapter adapter = new ArrayAdapter(ResultsAthlete.this, android.R.layout.simple_list_item_1,Result_List);

        final ListView lv = (ListView)findViewById(R.id.listView_results_athletes);
        lv.setAdapter(adapter);
    }
}
