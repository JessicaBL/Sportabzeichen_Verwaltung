package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class NewSports extends AppCompatActivity {

    DatabaseHelperSports myDbSp;
    double lat;
    double lng;
    String category="";
    String sports="";
    String unit="";

    //category 0-->endurance, 1-->strength, 2-->agility, 3-->coordination

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sports);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDbSp = new DatabaseHelperSports(this);

        Intent intent = getIntent();
        lat = intent.getDoubleExtra("lat", 0.0);
        lng = intent.getDoubleExtra("lng", 0.0);



        ListView lv = (ListView) findViewById(R.id.listView_sports);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                sports = String.valueOf(position);

                if(category.equals("0")) {
                    unit="min";
                    if(sports.equals("1")){
                        unit="h";
                    }
                }
                if(category.equals("1")) {
                    unit="m";
                    if(sports.equals("4")){
                        unit="Schwierigkeit";
                    }
                }
                if(category.equals("2")) {
                    unit="sek";
                    if(sports.equals("3")){
                        unit="Schwierigkeit";
                    }
                }
                if(category.equals("3")) {
                    unit="m";
                    if(sports.equals("0")){
                        unit="Schwierigkeit";
                    }
                    if(sports.equals("4")){
                        unit="Häufigkeit";
                    }
                }



            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_sports, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_sports) {

            if (category.equals("")) {
                Toast.makeText(NewSports.this, "Bitte Kategorie auswählen", Toast.LENGTH_LONG).show();
            }
            if (sports.equals("")) {
                Toast.makeText(NewSports.this, "Bitte Sportart auswählen", Toast.LENGTH_LONG).show();
            }
            else{
                boolean isInserted = myDbSp.insertData(category.toString(),
                        sports.toString(), unit.toString(), Double.toString(lat), Double.toString(lng));

                if (isInserted == true) {
                    Toast.makeText(NewSports.this, "Disziplin angelegt", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(NewSports.this, Maps.class);
                    startActivity(intent);
                } else
                    Toast.makeText(NewSports.this, "Disziplin nicht angelegt", Toast.LENGTH_LONG).show();
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


    public void onRadioButtonEnduranceClicked(View view) {
        RadioButton strength = (RadioButton)findViewById(R.id.radioButton_strength);
        RadioButton agility = (RadioButton)findViewById(R.id.radioButton_agility);
        RadioButton coordination = (RadioButton)findViewById(R.id.radioButton_coordination);
        sports="";
        category = "0";
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.endurance_array, android.R.layout.simple_list_item_1);

        final ListView lv = (ListView)findViewById(R.id.listView_sports);
        lv.setAdapter(adapter);
        strength.setChecked(false);
        agility.setChecked(false);
        coordination.setChecked(false);

    }

    public void onRadioButtonStrengthClicked(View view) {
        RadioButton endurance = (RadioButton)findViewById(R.id.radioButton_endurance);
        RadioButton agility = (RadioButton)findViewById(R.id.radioButton_agility);
        RadioButton coordination = (RadioButton)findViewById(R.id.radioButton_coordination);

        sports="";
        category = "1";
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.strength_array, android.R.layout.simple_list_item_1);

        final ListView lv = (ListView)findViewById(R.id.listView_sports);
        lv.setAdapter(adapter);
        endurance.setChecked(false);
        agility.setChecked(false);
        coordination.setChecked(false);

    }

    public void onRadioButtonAgilityClicked(View view) {
        RadioButton endurance = (RadioButton)findViewById(R.id.radioButton_endurance);
        RadioButton strength = (RadioButton)findViewById(R.id.radioButton_strength);
        RadioButton coordination = (RadioButton)findViewById(R.id.radioButton_coordination);

        sports="";
        category = "2";
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.agility_array, android.R.layout.simple_list_item_1);

        final ListView lv = (ListView)findViewById(R.id.listView_sports);
        lv.setAdapter(adapter);
        endurance.setChecked(false);
        strength.setChecked(false);
        coordination.setChecked(false);
    }

    public void onRadioButtonCoordinationClicked(View view) {
        RadioButton endurance = (RadioButton)findViewById(R.id.radioButton_endurance);
        RadioButton strength = (RadioButton)findViewById(R.id.radioButton_strength);
        RadioButton agility = (RadioButton)findViewById(R.id.radioButton_agility);

        sports="";
        category = "3";
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.coordination_array, android.R.layout.simple_list_item_1);

        final ListView lv = (ListView)findViewById(R.id.listView_sports);
        lv.setAdapter(adapter);
        endurance.setChecked(false);
        strength.setChecked(false);
        agility.setChecked(false);
    }



}
