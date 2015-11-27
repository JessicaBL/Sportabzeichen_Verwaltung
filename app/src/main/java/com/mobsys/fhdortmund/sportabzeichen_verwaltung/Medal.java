package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

public class Medal extends AppCompatActivity {

    String resultString;
    String sports, athlete;
    //Dummy-Parameter für 100m-Lauf männlich, 1990
    double bronze = 14.8;
    double silver = 13.5;
    double gold = 12.3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medal);

        Intent intent = getIntent();

       sports=intent.getStringExtra("sports");
        athlete =intent.getStringExtra("athlete");

        TextView medal_overview=(TextView)findViewById(R.id.textView_medal);
        medal_overview.setText("Medaille für Sportart "+sports);

        TextView medal_athlete=(TextView)findViewById(R.id.medal_sportler);
        medal_athlete.setText("Sportler: "+athlete);

        TextView medal_bronze=(TextView)findViewById(R.id.textView_bronze);
        medal_bronze.setText("Bronze: "+bronze);

        TextView medal_silver=(TextView)findViewById(R.id.textView_silver);
        medal_silver.setText("Silber: "+silver);

        TextView medal_gold=(TextView)findViewById(R.id.textView_gold);
        medal_gold.setText("Gold: "+gold);

        showMedal();
    }

    private void showMedal() {

        Intent intent = getIntent();

        resultString = intent.getStringExtra("result");
        double result = Double.parseDouble(resultString);


        TextView medal_result=(TextView)findViewById(R.id.medal_result);
        if (result>bronze){
            medal_result.setText("Sportler-Ergebnis: "+result+"--> Keine Medaille");
        }
        if (result<=bronze&&result>silver){
            medal_result.setText("Sportler-Ergebnis: "+result+"--> Bronze-Medaille");
        }
        if (result<=silver&&result>gold){
            medal_result.setText("Sportler-Ergebnis: "+result+"--> Silber-Medaille");
        }
        if (result<=gold){
            medal_result.setText("Sportler-Ergebnis: "+result+"--> Gold-Medaille");
        }
    }


    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch(keycode) {
            case KeyEvent.KEYCODE_BACK:
                Intent intent = new Intent(this, Maps.class);
                startActivity(intent);
                return true;
        }

        return super.onKeyDown(keycode, e);
    }

}
