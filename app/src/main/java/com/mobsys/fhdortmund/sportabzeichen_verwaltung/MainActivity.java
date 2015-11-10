package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Map;


public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new DatabaseHelper(this);
    }

    public void showAthleteOverview(View view) {
        Intent intent = new Intent(this, AthleteOverview.class);
        startActivity(intent);
    }

    public void showMap (View view){
        Intent intent = new Intent(this, Maps.class);
        startActivity(intent);
    }
}

