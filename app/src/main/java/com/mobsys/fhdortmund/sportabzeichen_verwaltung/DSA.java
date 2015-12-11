package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DSA extends AppCompatActivity {

    String endurance, strength, agility, coordination, athlete_id;
    DatabaseHelper myDb;
    DatabaseHelperSports myDbSp;
    DatabaseHelperResults myDbRs;
    DatabaseHelperPruefer myDbPr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ds);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        myDb = new DatabaseHelper(this);
        myDbSp = new DatabaseHelperSports(this);
        myDbRs = new DatabaseHelperResults(this);
        myDbPr = new DatabaseHelperPruefer(this);

        Intent intent = getIntent();
        endurance = intent.getStringExtra("endurance");
        strength = intent.getStringExtra("strength");
        agility = intent.getStringExtra("agility");
        coordination = intent.getStringExtra("coordination");
        athlete_id = intent.getStringExtra("athlete");


        getAthleteInfos(athlete_id);

        TextView pr1=(TextView) findViewById(R.id.textView_pr1);
        TextView pr2=(TextView) findViewById(R.id.textView_pr2);
        TextView pr3=(TextView) findViewById(R.id.textView_pr3);
        TextView pr4=(TextView) findViewById(R.id.textView_pr4);
        String[]endurance_split=endurance.split(",");
        String[]strength_split=strength.split(",");
        String[]agility_split=agility.split(",");
        String[]coordination_split=coordination.split(",");

        pr1.setText(endurance_split[0]);
        pr2.setText(strength_split[0]);
        pr3.setText(agility_split[0]);
        pr4.setText(coordination_split[0]);

        int sum_medal=calculate_medal(endurance_split[2], strength_split[2],agility_split[2], coordination_split[2]);

        TextView medal=(TextView) findViewById(R.id.textView_medal);

        if(sum_medal<8){
            medal.setText("Bronze");
        }
        else if(sum_medal<11){
            medal.setText("Silber");
        }
        else medal.setText("Gold");
    }




    public void getAthleteInfos(String athlete_id) {
        TextView athlete_name = (TextView) findViewById(R.id.textView_name);
        TextView athlete_birthday = (TextView) findViewById(R.id.textView_geb);

        Cursor res = myDb.selectSingleDataAthlete(athlete_id);
        res.moveToFirst();
        String name = res.getString(1);
        String surname = res.getString(2);
        String birthday = res.getString(3);

        String infos = name+" "+ surname;
        athlete_name.setText(infos);
        athlete_birthday.setText(birthday);
    }

    public  int calculate_medal(String endurance, String strength, String agility, String coordination) {
        int sum=0;
        if (endurance.contains("Bronze"))sum=+1;
        if (endurance.contains("Silber"))sum=+2;
        if (endurance.contains("Gold"))sum=+3;

        if (strength.contains("Bronze"))sum=+1;
        if (strength.contains("Silber"))sum=+2;
        if (strength.contains("Gold"))sum=+3;

        if (agility.contains("Bronze"))sum=+1;
        if (agility.contains("Silber"))sum=+2;
        if (agility.contains("Gold"))sum=+3;

        if (coordination.contains("Bronze"))sum=+1;
        if (coordination.contains("Silber"))sum=+2;
        if (coordination.contains("Gold"))sum=+3;

        return sum;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dsa, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
            return true;
        }

        if(id==R.id.action_uploadDSA){

        }
            return super.onOptionsItemSelected(item);

        }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch(keycode) {
            case KeyEvent.KEYCODE_BACK:
                Intent intent = new Intent(this, DSA.class);
                startActivity(intent);
                return true;
        }

        return super.onKeyDown(keycode, e);
    }
    }

