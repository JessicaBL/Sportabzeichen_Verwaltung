package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    DatabaseHelperPruefer myDbPr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new DatabaseHelper(this);
        myDbPr = new DatabaseHelperPruefer(this);
        setPrueferName();

    }

    private void setPrueferName() {

        Intent intent = getIntent();
        String id_pruefer;
        id_pruefer =intent.getStringExtra("id_pruefer");

        Cursor res = myDbPr.getName(id_pruefer);
        res.moveToFirst();
        String name=res.getString(1);

        TextView loginAs=(TextView)findViewById(R.id.login_as);
        loginAs.setText("Eingeloggt als " + name);
    }

    public void showMap(View view){
        Intent intent = new Intent(this,Maps.class);
        startActivity(intent);
    }

    public void showAthleteOverview(View view) {
        Intent intent = new Intent(this, AthleteOverview.class);
        startActivity(intent);
    }

    public void logout(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Prüfer-Logout");
        builder.setCancelable(false);
        builder.setPositiveButton("Ausloggen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);


            }

        });
        builder.setNegativeButton("abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch(keycode) {
            case KeyEvent.KEYCODE_BACK:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("App beenden?");
                builder.setCancelable(false);
                builder.setPositiveButton("Beenden", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);

                        Intent intent1 = new Intent(getApplicationContext(), Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent1);

                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();


                return true;
        }

        return super.onKeyDown(keycode, e);
    }

}

