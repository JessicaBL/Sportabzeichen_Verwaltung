package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    ArrayList<String> AthletesList = new ArrayList<>();
    ArrayAdapter<String> adapter_athletes;

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

        Cursor resPr = myDbPr.getActive();
        resPr.moveToFirst();
        id_pruefer = resPr.getString(0);


        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        Cursor res = myDbSp.selectSingleData(id);

        while (res.moveToNext()) {
            id_sports = res.getString(0);
            category = res.getString(1);
            sports = res.getString(2);
            unit = res.getString(3);
        }



        customizeSupportActionBar(category, sports);

        AthletesList = populateSpinner();

        spinner_athlete = (Spinner) findViewById(R.id.spinner_athlete);
        result = (EditText) findViewById(R.id.editText_result);
        adapter_athletes = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                AthletesList);

        adapter_athletes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_athlete.setAdapter(adapter_athletes);

        TextView sports_text = (TextView) findViewById(R.id.textView_unit);
        sports_text.setText("Ergebnis in " + unit);

       // final ImageButton button = (ImageButton) findViewById(R.id.button_search);
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//                search(adapter_athletes, AthletesList);
//
//
//            }
//        });

        spinner_athlete.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if(category.equals("0")&&(sports.equals("3"))) {
                    adjustParameter(0, 3, selected);
                }
                if(category.equals("1")&&(sports.equals("1"))) {
                    adjustParameter(1, 1, selected);
                }
                if(category.equals("1")&&(sports.equals("2"))) {
                    adjustParameter(1, 2, selected);
                }
                if(category.equals("2")&&(sports.equals("0"))) {
                    adjustParameter(0, 3, selected);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




    }

    public void adjustParameter(int category, int sports, String selected) {
        String selected_info[] = selected.split(" ");
        String selected_id = selected_info[0];

        Cursor res = myDb.selectSingleDataAthlete(selected_id);
        res.moveToFirst();
        String birthday = res.getString(3);
        String sex = res.getString(4);

        String birthday_split[] = birthday.split(" ");
        int birthyear = Integer.parseInt(birthday_split[2]);


        Calendar c = Calendar.getInstance();
        int cur_year = c.get(Calendar.YEAR);
        int dif=cur_year-birthyear;
        String parameter="";
        if(category==0 && sports==3) {
            if (dif <= 49) parameter="800 m";
            else if (dif <= 74)  parameter="600 m";
            else if (dif >= 75)  parameter="200 m";
        }
        if(category==1 && sports==1){
            if (dif <= 49 && sex.equals("weiblich")) parameter="4 kg";
            else if (dif <= 74 &&sex.equals("weiblich")) parameter="3 kg";
            else if (dif >= 75 && sex.equals("weiblich")) parameter="2 kg";

            else if (dif <= 19 && sex.equals("männlich")) parameter="6 kg";
            else if (dif <= 49 && sex.equals("männlich")) parameter="7,26 kg";
            else if (dif <= 59 && sex.equals("männlich")) parameter="6 kg";
            else if (dif <= 69 && sex.equals("männlich")) parameter="5 kg";
            else if (dif <= 79 && sex.equals("männlich")) parameter="4 kg";
            else if (dif >=80 && sex.equals("männlich")) parameter="3 kg";
        }
        if(category==1 && sports==2){
            if (dif <= 19 && sex.equals("männlich")) parameter="10 kg";
            else if (dif <= 49 && sex.equals("männlich")) parameter="15 kg";
            else if (dif >= 50 && sex.equals("männlich")) parameter="10 kg";
            else if (sex.equals("weiblich")) parameter="5 kg";
        }
        if(category==2 && sports==0){
            if (dif <= 39) parameter="100 m";
            else if (dif <= 74) parameter="50 m";
            else if (dif >= 75) parameter="30 m";
        }
        getSupportActionBar().setSubtitle("Ergebnis eintragen ("+parameter+")");

    }

    public void search(ArrayAdapter adapter_athletes, final ArrayList AthletesList) {

        spinner_athlete.setAdapter(adapter_athletes);

        LayoutInflater li = LayoutInflater.from(NewResult.this);

        final View promptsView = li.inflate(R.layout.search_layout, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(NewResult.this);

        final EditText input = (EditText) promptsView.findViewById(R.id.search_edittext);



        alert.setTitle("Nach Sportler suchen");
        alert.setView(promptsView);


        alert.setPositiveButton("Suche", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String result = input.getText().toString();
                ArrayList<String> Original_List = AthletesList;
                ArrayList<String> AthletesList_Search = new ArrayList<String>();

                for (String s : Original_List) {
                    if (s.contains(result)==true) {
                        AthletesList_Search.add(s);
                    }
                }

                if (AthletesList_Search.isEmpty()) {
                    Toast.makeText(NewResult.this, "Keinen Sportler gefunden", Toast.LENGTH_LONG).show();
                    dialog.cancel();
                } else {
                    ArrayAdapter<String> adapter_athletes_search = new ArrayAdapter<String>(
                            NewResult.this,
                            android.R.layout.simple_spinner_item,
                            AthletesList_Search);

                    adapter_athletes_search.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_athlete.setAdapter(adapter_athletes_search);
                }
            }

        });
        alert.setNegativeButton("abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    public void customizeSupportActionBar(String sport_category, String sport_name) {
        Resources res_end = getResources();
        if(sport_category.equals("0")){
            String[] endurance = res_end.getStringArray(R.array.endurance_array);
            getSupportActionBar().setTitle("Ausdauer: " + endurance[Integer.valueOf(sport_name)]);
        }
        if(sport_category.equals("1")){
            String[] strength = res_end.getStringArray(R.array.strength_array);
            getSupportActionBar().setTitle("Kraft: " + strength[Integer.valueOf(sport_name)]);
        }
        if(sport_category.equals("2")){
            String[] agility = res_end.getStringArray(R.array.agility_array);
            getSupportActionBar().setTitle("Schnelligkeit: " + agility[Integer.valueOf(sport_name)]);
        }
        if(sport_category.equals("3")){
            String[] coordination = res_end.getStringArray(R.array.coordination_array);
            getSupportActionBar().setTitle("Koordination: " + coordination[Integer.valueOf(sport_name)]);
        }
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

        if (id==R.id.action_search){
            search(adapter_athletes,AthletesList);
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
