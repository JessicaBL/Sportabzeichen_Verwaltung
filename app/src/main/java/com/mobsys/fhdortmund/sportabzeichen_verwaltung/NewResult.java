package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class NewResult extends AppCompatActivity {

    DatabaseHelper myDb;
    DatabaseHelperSports myDbSp;
    String id;
    String id_sports;
    String id_athlete;
    Spinner spinnerAthlete;
    DatabaseHelperResults myDbRs;
    EditText result, resultNr;
    Button btnAddResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_result);

        myDb = new DatabaseHelper(this);
        myDbSp = new DatabaseHelperSports(this);
        myDbRs = new DatabaseHelperResults(this);


        Intent intent = getIntent();
       id =intent.getStringExtra("id");


         Cursor res = myDbSp.selectSingleData(id);

        id_sports = null;
        String name= null;
        String unity = null;

        while(res.moveToNext()) {
            id_sports = res.getString(0);
            name = res.getString(1);
            unity = res.getString(2);
        }

       TextView sports_text=(TextView)findViewById(R.id.sports_text);
       sports_text.setText("Ergebnisse für Sportart '"+ name+"' eintragen");

        TextView sports_text_unity=(TextView)findViewById(R.id.sports_text_unity);
        sports_text_unity.setText(""+unity+"");



        ArrayList<String> AthletesList = populateSpinner();

       spinnerAthlete = (Spinner) findViewById(R.id.spinner_athlete);

       ArrayAdapter<String> adapter_athletes= new ArrayAdapter<String>(
              this,
              android.R.layout.simple_spinner_item,
              AthletesList);

        adapter_athletes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAthlete.setAdapter(adapter_athletes);



        result = (EditText) findViewById(R.id.editTextresult);
        resultNr = (EditText) findViewById(R.id.editTextresultNr);
        btnAddResult = (Button) findViewById(R.id.result_add);
        AddData();

    }


    //Daten für Sportler-Spinner erzeugen
    public ArrayList<String> populateSpinner(){
        Cursor res = myDb.getAllData();

        ArrayList<String> AthletesList = new ArrayList<String>();

        while(res.moveToNext()){
            String ID=res.getString(0);
            String name=res.getString(1);
            String surname=res.getString(2);
            AthletesList.add(ID+", "+name+" "+ surname);
        }

        return AthletesList;
    }



    private void AddData() {
        btnAddResult.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
        String athlete =String.valueOf(spinnerAthlete.getSelectedItem());
        String[] splitResult = athlete.split(",");
        String id_athlete = splitResult[0];


        if (result.getText().toString().equals("")) {
            Toast.makeText(NewResult.this, "Bitte Ergebnis eingeben", Toast.LENGTH_LONG).show();
        } else if (resultNr.getText().toString().equals("")) {
            Toast.makeText(NewResult.this, "Bitte Ergebnis-Nr. eingeben", Toast.LENGTH_LONG).show();
        }
            else {
                boolean isInserted = myDbRs.insertData(id_athlete.toString(), id_sports.toString()
                        , result.getText().toString(), resultNr.getText().toString());

                if (isInserted == true) {
                    Toast.makeText(NewResult.this, "Ergebnis eingetragen", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(NewResult.this, Maps.class);
                    startActivity(intent);
                }
                else
                    Toast.makeText(NewResult.this, "Ergebnis eingetragen", Toast.LENGTH_LONG).show();
            }
    }
    });

}
}





