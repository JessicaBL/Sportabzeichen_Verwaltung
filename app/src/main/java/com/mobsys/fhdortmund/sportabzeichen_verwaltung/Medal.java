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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Medal extends AppCompatActivity {


    DatabaseHelper myDb;
    DatabaseHelperSports myDbSp;
    DatabaseHelperResults myDbRs;
    DatabaseHelperPruefer myDbPr;

    Spinner spinner_athlete;
    String athlete_id="", result_endurance="";


    //300m lauf
    double endurance_bronze= 17;
    double endurance_silver= 15;
    double endurance_gold = 13;

    //standweitsprung
    double strength_bronze=2.05;
    double strength_silver=2.25;
    double strength_gold=2.45;

    //25m Schwimmen
    double agility_bronze=14.8;
    double agility_silver=13.5;
    double agility_gold=12.3;

    //Hochsprung
    double coordination_bronze=4.30;
    double coordination_silver=4.60;
    double coordination_gold=4.90;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDb = new DatabaseHelper(this);
        myDbSp = new DatabaseHelperSports(this);
        myDbRs = new DatabaseHelperResults(this);
        myDbPr = new DatabaseHelperPruefer(this);

        ArrayList<String> AthletesList = populateSpinner();

        GetIntent();


        spinner_athlete = (Spinner) findViewById(R.id.spinner_athlete_medal);
        ArrayAdapter<String> adapter_athletes = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                AthletesList);

        adapter_athletes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_athlete.setAdapter(adapter_athletes);

        spinner_athlete.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = String.valueOf(spinner_athlete.getSelectedItem());
                String[] split_Result=item.split(" ");
                athlete_id=split_Result[0];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




    }

    public void GetIntent() {
        Intent intent = getIntent();
        result_endurance = intent.getStringExtra("result_endurance");
        TextView sports_text = (TextView) findViewById(R.id.textView_medal_endurance);
        sports_text.setText(result_endurance);
    }


    public ArrayList<String> populateSpinner() {
        Cursor res = myDb.getAllData();

        ArrayList<String> AthletesList = new ArrayList<String>();

        while (res.moveToNext()) {
            String ID = res.getString(0);
            String name = res.getString(1);
            String surname = res.getString(2);
            AthletesList.add(ID + " - " + name + " " + surname);
        }

        return AthletesList;
    }

    public void ListView_endurance(View view){
        Intent intent = new Intent(this, Medal_ListView.class);
        intent.putExtra("id_athlete", athlete_id);
        intent.putExtra("category", "0");
        startActivity(intent);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_athlete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_saveDSA) {

        }
        if (id==android.R.id.home){

            Intent intent = new Intent(this, AthleteOverview.class);
            startActivity(intent);
            this.finish();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

}
