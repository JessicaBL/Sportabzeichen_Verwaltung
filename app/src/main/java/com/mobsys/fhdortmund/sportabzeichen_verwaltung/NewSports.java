package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class NewSports extends AppCompatActivity {

    DatabaseHelperSports myDbSp;
    EditText editName, editInfo, editID;
    double lat;
    double lng;

    Button btnAddData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sports);

        myDbSp = new DatabaseHelperSports(this);

        editName = (EditText) findViewById(R.id.editText_sportsName);

        Intent intent = getIntent();
        lat = intent.getDoubleExtra("lat", 0.0);
        lng = intent.getDoubleExtra("lng", 0.0);

        btnAddData = (Button) findViewById(R.id.button_addSports);
        AddData();

    }

    private void AddData() {
        btnAddData.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        RadioButton meter= (RadioButton)findViewById(R.id.radioButton_meter);
                        RadioButton seconds= (RadioButton)findViewById(R.id.radioButton_seconds);
                        String unity="Meter";
                        if(seconds.isChecked()) unity="Sekunden";

                        if (editName.getText().toString().equals("")) {
                            Toast.makeText(NewSports.this, "Bitte Name der Sportart angeben", Toast.LENGTH_LONG).show();
                        }
                          else if(!meter.isChecked()&& !seconds.isChecked()) {
                            Toast.makeText(NewSports.this, "Bitte die Einheit der Sportart angeben", Toast.LENGTH_LONG).show();

                        } else {
                            boolean isInserted = myDbSp.insertData(editName.getText().toString(),
                                    unity,Double.toString(lat), Double.toString(lng));

                            if (isInserted == true) {
                                Toast.makeText(NewSports.this, "Disziplin angelegt", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(NewSports.this, Maps.class);
                                startActivity(intent);
                            }
                            else
                                Toast.makeText(NewSports.this, "Disziplin nicht angelegt", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    public void onRadioButtonMeterClicked (View view) {
        RadioButton seconds= (RadioButton)findViewById(R.id.radioButton_seconds);
        seconds.setChecked(false);
    }
    public void onRadioButtonSecondsClicked(View view) {
        RadioButton meter= (RadioButton)findViewById(R.id.radioButton_meter);
        meter.setChecked(false);
    }

}

