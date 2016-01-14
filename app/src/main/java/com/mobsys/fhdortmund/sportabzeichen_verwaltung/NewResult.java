package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
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
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class NewResult extends AppCompatActivity {

    DatabaseHelper myDb;
    DatabaseHelperSports myDbSp;
    DatabaseHelperResults myDbRs;
    DatabaseHelperCondition myDbCon;
    DatabaseHelperParameter myDbPar;

    SessionManager session;

    String id_athlete;
    Spinner spinner_athlete;
    EditText result;

    int id_sports;
    String category = null;
    String unit = null;
    String parameter = null;
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
        myDbCon = new DatabaseHelperCondition(this);
        myDbPar = new DatabaseHelperParameter(this);

        session = new SessionManager(getApplicationContext());

        id_pruefer = session.getUserId();


        Intent intent = getIntent();
        sports = intent.getStringExtra("sports");
        Cursor res = myDbSp.selectSingleSports(sports);

        res.moveToFirst();
        id_sports = res.getInt(0);
        category = res.getString(1);
        unit = res.getString(3);
        parameter = res.getString(4);


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


        spinner_athlete.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();

                if(parameter.equals("1")) {
                    updateParameter(selected);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void updateParameter(String selected) {
        String selected_info[] = selected.split(" ");
        String selected_id = selected_info[0];

        Cursor res = myDb.selectSingleDataAthlete(selected_id);
        res.moveToFirst();
        String sex = res.getString(3);
        String birthday = res.getString(4);

        String birthday_split[] = birthday.split(" ");
        int birthyear = Integer.parseInt(birthday_split[2]);


        Calendar c = Calendar.getInstance();
        int cur_year = c.get(Calendar.YEAR);
        int age=cur_year-birthyear;

        Cursor resCon = myDbCon.selectSingleData(age, sex);

        if(resCon.getCount()>0) {

            resCon.moveToFirst();
            int conId = res.getInt(0);
            Cursor resPar = myDbPar.selectSingleParameter(id_sports, conId);

            if(resPar.getCount() > 0) {
                resPar.moveToFirst();
                String parameter = resPar.getString(0);
                getSupportActionBar().setSubtitle("Ergebnis eintragen ("+parameter+")");
            } else {
                Log.d("Updating parameter", "No Parameter found");
            }
        } else {
            Log.d("Updating parameter", "No row found with specified conditions");
        }

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
            getSupportActionBar().setTitle(sport_category + ": " + sport_name);

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

            }else if(checkTries(id_athlete, Integer.toString(id_sports))==false){
                Toast.makeText(NewResult.this, "Anzahl der Versuche für diese Disziplin überschritten", Toast.LENGTH_LONG).show();
            } else {
                UUID uuid = UUID.randomUUID();
                String result_id = uuid.toString();

                String currentDateString = DateFormat.getDateInstance().format(new Date());

                boolean isInserted = myDbRs.insertData(result_id, id_pruefer, id_athlete, Integer.toString(id_sports), result.getText().toString(), currentDateString.toString(), "0");

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

    public  boolean checkTries(String id_athlete, String id_sports) {
        Cursor res_results= myDbRs.selectSingleDataAthlete(id_athlete);
        Calendar c = Calendar.getInstance();
        String cur_year =new Integer(c.get(Calendar.YEAR)).toString();

        int counter=0;
        while(res_results.moveToNext()){
            String sports_id_result= res_results.getString(3);
            String sports_date= res_results.getString(5);

            String sports_dateSplit[]=sports_date.split("\\.");

            if(cur_year.equals(sports_dateSplit[2]) && sports_id_result.equals(id_sports)){
                counter++;
            }
        }
        if (counter<=3) return true;
        else return false;

    }
}
