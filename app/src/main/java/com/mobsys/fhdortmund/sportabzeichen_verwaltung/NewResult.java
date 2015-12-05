package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewResult extends AppCompatActivity {

    DatabaseHelper myDb;
    DatabaseHelperSports myDbSp;
    DatabaseHelperResults myDbRs;
    DatabaseHelperPruefer myDbPr;

    String id;
    String id_athlete;
    Spinner spinner_athlete;
    EditText result;

    String id_sports = null;
    String category = null;
    String unit = null;
    String sports = null;

    String id_pruefer = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        myDb = new DatabaseHelper(this);
        myDbSp = new DatabaseHelperSports(this);
        myDbRs = new DatabaseHelperResults(this);
        myDbPr = new DatabaseHelperPruefer(this);


        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        Cursor res = myDbSp.selectSingleData(id);

        Cursor resPr = myDbPr.getActive();
        resPr.moveToFirst();
        id_pruefer = resPr.getString(0);


        while (res.moveToNext()) {
            id_sports = res.getString(0);
            category = res.getString(1);
            sports = res.getString(2);
            unit = res.getString(3);
        }
        Resources res_str = getResources();
        String[] sport_name = new String[0];
        if (category.equals(0)) {
            sport_name = res_str.getStringArray(R.array.endurance_array);
        }
        if (category.equals(1)) {
            sport_name = res_str.getStringArray(R.array.strength_array);
        }
        if (category.equals(2)) {
            sport_name = res_str.getStringArray(R.array.agility_array);
        }
        if (category.equals(3)) {
            sport_name = res_str.getStringArray(R.array.coordination_array);
        }
//        String name = sport_name[Integer.valueOf(sports)];
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        this.setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);
        //  getSupportActionBar().setTitle("Ergebnis Sportart " +name);

        ArrayList<String> AthletesList = populateSpinner();

        spinner_athlete = (Spinner) findViewById(R.id.spinner_athlete);
        result = (EditText) findViewById(R.id.editText_result);
        ArrayAdapter<String> adapter_athletes = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                AthletesList);

        adapter_athletes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_athlete.setAdapter(adapter_athletes);

        TextView sports_text = (TextView) findViewById(R.id.textView_unit);
        sports_text.setText("Ergebnis in " + unit);


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_result, menu);
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
            String athlete = String.valueOf(spinner_athlete.getSelectedItem());

            String[] splitResult = athlete.split(" ");
            String id_athlete = splitResult[0];

            if (spinner_athlete.getCount()==0){
                Toast.makeText(NewResult.this, "Kein Sportler vorhanden", Toast.LENGTH_LONG).show();
            }
            else if (result.getText().toString().equals("")) {
                  Toast.makeText(NewResult.this, "Bitte Ergebnis eingeben", Toast.LENGTH_LONG).show();
            } else {
                    String currentDateString = DateFormat.getDateInstance().format(new Date());

                    boolean isInserted = myDbRs.insertData(id_athlete.toString(), id_sports.toString()
                            , result.getText().toString(), currentDateString.toString(), id_pruefer.toString());

                    if (isInserted == true) {
                        Toast.makeText(NewResult.this, "Ergebnis eingetragen", Toast.LENGTH_LONG).show();
                        Intent intent1 = new Intent(NewResult.this, Maps.class);
                        startActivity(intent1);
                    } else
                        Toast.makeText(NewResult.this, "Ergebnis nicht eingetragen", Toast.LENGTH_LONG).show();
                }


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
