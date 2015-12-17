package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.util.ArrayList;

public class ShowResults extends AppCompatActivity {

    DatabaseHelper myDb;
    DatabaseHelperSports myDbSp;
    DatabaseHelperResults myDbRs;

    SessionManager session;

    ArrayList<String> Result_List= new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        myDb = new DatabaseHelper(this);
        myDbSp = new DatabaseHelperSports(this);
        myDbRs = new DatabaseHelperResults(this);

        session = new SessionManager(getApplicationContext());

        populateListView();


    }

    public void populateListView() {
        Intent intent = getIntent();
        String sport_name =intent.getStringExtra("sports");


        Cursor res_Sports = myDbSp.selectSingleSports(sport_name);
        res_Sports.moveToFirst();
        String sport_id= res_Sports.getString(0);
        String sport_category = res_Sports.getString(1);
        String sport_unit = res_Sports.getString(3);

        customizeSupportActionBar(sport_category, sport_name);

        Cursor res_Results = myDbRs.selectSingleDataSports(sport_id);

        while (res_Results.moveToNext()) {

            String id_pruefer = res_Results.getString(1);
            String id_athlete = res_Results.getString(2);
            String result = res_Results.getString(4);
            String result_nr = res_Results.getString(5);

            String name;
            String surname;
            Cursor res_Athlete = myDb.selectSingleDataAthlete(id_athlete);
            if(res_Athlete.getCount()==0){
                continue;
            }
            else {
                res_Athlete.moveToFirst();
                name = res_Athlete.getString(1);
                surname = res_Athlete.getString(2);
            }

            String cur_pruefer_id = session.getUserId();

            if (cur_pruefer_id.equals(id_pruefer)) {
                Result_List.add(name + " " + surname + ": " + result + " " + sport_unit + ", Datum: " + result_nr);
            } else {
                Result_List.add("Ergebnis von " + name + " " + surname + " für Prüfer unsichtbar, Datum: " + result_nr);
            }

        }


        if(Result_List.isEmpty()){
            Result_List.add("Keine Ergebnisse vorhanden");

        }

        ListAdapter adapter = new ArrayAdapter(ShowResults.this, android.R.layout.simple_list_item_1,Result_List);

        final ListView lv = (ListView)findViewById(R.id.listView_results_all);
        lv.setAdapter(adapter);


    }

    public  void customizeSupportActionBar(String sport_category, String sport_name) {
        getSupportActionBar().setTitle(sport_category +": "+ sport_name);
        getSupportActionBar().setSubtitle("Ergebnisse");
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_result) {

        }
        if (id==android.R.id.home){

            Intent intent = new Intent(this, Maps.class);
            startActivity(intent);
            this.finish();
            return true;

        }

            return super.onOptionsItemSelected(item);


    }
}
