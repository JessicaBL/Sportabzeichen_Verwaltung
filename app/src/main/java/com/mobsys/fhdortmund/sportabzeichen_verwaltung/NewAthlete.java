package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

public class NewAthlete extends AppCompatActivity {

    DatabaseHelper myDb;
    EditText editName, editSurname, editTextId;
    Spinner spinnerBirthdayMonth, spinnerBirthdayDay, spinnerBirthdayYear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_athlete);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myDb = new DatabaseHelper(this);

        editName = (EditText) findViewById(R.id.editText_name);
        editSurname = (EditText) findViewById(R.id.editText_surname);

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
            RadioButton female= (RadioButton)findViewById(R.id.radioButton_female);
            RadioButton male= (RadioButton)findViewById(R.id.radioButton_male);
            String sex="männlich";
            if(female.isChecked())
                sex="weiblich";

            if(editName.getText().toString().equals("")){
                Toast.makeText(NewAthlete.this, "Bitte Vornamen angeben", Toast.LENGTH_LONG).show();
            }
            else if(editSurname.getText().toString().equals("")){
                Toast.makeText(NewAthlete.this, "Bitte Nachnamen angeben", Toast.LENGTH_LONG).show();
            }
            else if(!male.isChecked()&& !female.isChecked()) {
                Toast.makeText(NewAthlete.this, "Bitte Geschlecht angeben", Toast.LENGTH_LONG).show();
            }
            else {
                boolean isInserted = myDb.insertData(editName.getText().toString(),
                        editSurname.getText().toString(),
                        String.valueOf(spinnerBirthdayDay.getSelectedItem()) + "." +
                                String.valueOf(spinnerBirthdayMonth.getSelectedItem()) + "." +
                                String.valueOf(spinnerBirthdayYear.getSelectedItem()),
                        sex);

                if (isInserted == true) {
                    Toast.makeText(NewAthlete.this, "Sportler angelegt", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(NewAthlete.this, AthleteOverview.class);
                    startActivity(intent);
                }
                else
                    Toast.makeText(NewAthlete.this, "Sportler nicht angelegt", Toast.LENGTH_LONG).show();
            }



}
        return super.onOptionsItemSelected(item);
    }

    public void onRadioButtonMaleClicked(View view) {
        RadioButton female= (RadioButton)findViewById(R.id.radioButton_female);
        female.setChecked(false);
    }
    public void onRadioButtonFemaleClicked(View view) {
        RadioButton male= (RadioButton)findViewById(R.id.radioButton_male);
        male.setChecked(false);
    }


}
