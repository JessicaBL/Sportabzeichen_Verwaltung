package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class UpdateAthlete extends AppCompatActivity {

    DatabaseHelper myDb;
    EditText editName, editSurname;
    Spinner spinnerBirthdayMonth, spinnerBirthdayDay, spinnerBirthdayYear;
    RadioButton male, female;
    String editSex, birthdayDay, birthdayMonth, birthdayYear, currentString;
    String[] separated;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_athlete);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sportler bearbeiten");

        myDb = new DatabaseHelper(this);

        editName = (EditText) findViewById(R.id.editText_name);
        editSurname = (EditText) findViewById(R.id.editText_surname);
        female = (RadioButton) findViewById(R.id.radioButton_female);
        male = (RadioButton) findViewById(R.id.radioButton_male);


        spinnerBirthdayMonth = (Spinner) findViewById(R.id.spinner_month);
        ArrayAdapter<CharSequence> adapter_month = ArrayAdapter.createFromResource(this,
                R.array.month_array, android.R.layout.simple_spinner_item);
        adapter_month.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBirthdayMonth.setAdapter(adapter_month);

        spinnerBirthdayDay = (Spinner) findViewById(R.id.spinner_day);
        ArrayAdapter<CharSequence> adapter_day = ArrayAdapter.createFromResource(this,
                R.array.days_array, android.R.layout.simple_spinner_item);
        adapter_day.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBirthdayDay.setAdapter(adapter_day);

        spinnerBirthdayYear = (Spinner) findViewById(R.id.spinner_year);
        ArrayAdapter<CharSequence> adapter_year = ArrayAdapter.createFromResource(this,
                R.array.years_array, android.R.layout.simple_spinner_item);
        adapter_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBirthdayYear.setAdapter(adapter_year);

        updateAthleteData();

    }

    public void updateAthleteData() {

        Intent intent = getIntent();
        String id_athlete = intent.getStringExtra("id_athlete");
        Cursor athleteData = myDb.selectSingleDataAthlete(id_athlete);
        athleteData.moveToFirst();
        editName.setText(athleteData.getString(1));
        editSurname.setText(athleteData.getString(2));

        currentString = athleteData.getString(4);
        separated = currentString.split(" ");

        birthdayDay = removeLastChar(separated[0]); //the value you want the position for
        birthdayMonth = separated[1];
        birthdayYear = separated[2];

        ArrayAdapter adapBirthdayDay = (ArrayAdapter) spinnerBirthdayDay.getAdapter(); //cast to an ArrayAdapter
        int spinnerPositionDay = adapBirthdayDay.getPosition(birthdayDay);
        spinnerBirthdayDay.setSelection(spinnerPositionDay);

        ArrayAdapter adapBirthdayMonth = (ArrayAdapter) spinnerBirthdayMonth.getAdapter(); //cast to an ArrayAdapter
        int spinnerPositionMonth = adapBirthdayMonth.getPosition(birthdayMonth);
        spinnerBirthdayMonth.setSelection(spinnerPositionMonth);

        ArrayAdapter adapBirthdayYear = (ArrayAdapter) spinnerBirthdayYear.getAdapter(); //cast to an ArrayAdapter
        int spinnerPositionYear = adapBirthdayYear.getPosition(birthdayYear);
        spinnerBirthdayYear.setSelection(spinnerPositionYear);

        editSex = athleteData.getString(3);
        if (editSex.equals("weiblich"))
            female.setChecked(true);
        else
            male.setChecked(true);
    }

    private static String removeLastChar(String str) {
        return str.substring(0,str.length()-1);
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
        if (id == R.id.action_add_athlete) {
            if (female.isChecked())
                editSex = "weiblich";
            else
                editSex = "männlich";

            if (editName.getText().toString().equals("")) {
                Toast.makeText(UpdateAthlete.this, "Bitte Vornamen angeben", Toast.LENGTH_LONG).show();
            } else if (editSurname.getText().toString().equals("")) {
                Toast.makeText(UpdateAthlete.this, "Bitte Nachnamen angeben", Toast.LENGTH_LONG).show();
            } else if (!male.isChecked() && !female.isChecked()) {
                Toast.makeText(UpdateAthlete.this, "Bitte Geschlecht angeben", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = getIntent();
                String id_athlete = intent.getStringExtra("id_athlete");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = new Date();
                String current_timestamp = dateFormat.format(date);
                boolean isInserted = myDb.updateData(id_athlete, editName.getText().toString(),
                        editSurname.getText().toString(),editSex,
                        String.valueOf(spinnerBirthdayDay.getSelectedItem()) + ". " +
                                String.valueOf(spinnerBirthdayMonth.getSelectedItem()) + " " +
                                String.valueOf(spinnerBirthdayYear.getSelectedItem()),
                        current_timestamp, "0");

                if (isInserted == true) {
                    Toast.makeText(UpdateAthlete.this, "Sportler bearbeitet", Toast.LENGTH_LONG).show();
                    intent = new Intent(UpdateAthlete.this, AthleteOverview.class);
                    startActivity(intent);
                    this.finish();
                } else
                    Toast.makeText(UpdateAthlete.this, "Sportler nicht bearbeitet", Toast.LENGTH_LONG).show();
            }


        }
        if (id == android.R.id.home) {

            Intent intent = new Intent(this, AthleteOverview.class);
            startActivity(intent);
            this.finish();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void onRadioButtonMaleClicked(View view) {
        RadioButton female = (RadioButton) findViewById(R.id.radioButton_female);
        female.setChecked(false);
    }

    public void onRadioButtonFemaleClicked(View view) {
        RadioButton male = (RadioButton) findViewById(R.id.radioButton_male);
        male.setChecked(false);
    }
}
