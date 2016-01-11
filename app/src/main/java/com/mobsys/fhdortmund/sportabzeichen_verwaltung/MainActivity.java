package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session.checkLogin();
        setPrueferName();

        if (session.isLoggedIn()){
            SyncUtils.CreateSyncAccount(getApplicationContext());
        }
    }

    private void setPrueferName() {

        String name = session.getUserName();
        TextView loginAs = (TextView) findViewById(R.id.textView_loginAs);
        loginAs.setText("Eingeloggt als " + name);
    }

    public void showMap(View view){
        Intent intent = new Intent(this, Maps.class);
        startActivity(intent);
    }

    public void showAthleteOverview(View view){
        Intent intent = new Intent(this, AthleteOverview.class);
        startActivity(intent);
    }

    public void showMedal(View view){
        Intent intent = new Intent(this, Medal.class);
        startActivity(intent);
    }

    public void showHelp(View view){
        Intent intent = new Intent(this, Help.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Logout durchführen?");
            builder.setCancelable(false);
            builder.setPositiveButton("Ausloggen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    session.logoutUser();
                }
            });
            builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        }
        return super.onOptionsItemSelected(item);
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

                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
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
