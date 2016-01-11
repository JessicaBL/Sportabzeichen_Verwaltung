package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog pDialog;

    SessionManager session;
    DatabaseHelper myDb;
    DatabaseHelperCondition myDbCon;
    DatabaseHelperParameter myDbPar;
    DatabaseHelperResults myDbRs;
    DatabaseHelperSports myDbSp;
    DatabaseHelperStation myDbSt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session.checkLogin();
        setPrueferName();

        myDb = new DatabaseHelper(this);
        myDbCon = new DatabaseHelperCondition(this);
        myDbPar = new DatabaseHelperParameter(this);
        myDbRs = new DatabaseHelperResults(this);
        myDbSp = new DatabaseHelperSports(this);
        myDbSt = new DatabaseHelperStation(this);

        boolean isSynced = session.getDbSynced();

        if(!isSynced && session.isLoggedIn()){
            DbSyncTask dbSyncTask = new DbSyncTask();
            dbSyncTask.execute();
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

    class DbSyncTask extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Show progress dialog during task execution
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Datenbanksynchronisation..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            boolean successfulSyncAthl = false;
            boolean successfulSyncCon = false;
            boolean successfulSyncPar = false;
            boolean successfulSyncRs = false;
            boolean successfulSyncSp = false;
            boolean successfulSyncSt = false;

            Cursor res_athl = myDb.getAllData();
            if(res_athl.getCount()==0){
                successfulSyncAthl = myDb.getInitialServerData(getApplicationContext());
            } else {
                successfulSyncAthl = true;
            }

            Cursor res_con = myDbCon.getAllData();
            if(res_con.getCount()==0){
                successfulSyncCon = myDbCon.getInitialServerData(getApplicationContext());
            } else {
                successfulSyncCon = true;
            }

            Cursor res_par = myDbPar.getAllData();
            if(res_par.getCount()==0){
                successfulSyncPar = myDbPar.getInitialServerData(getApplicationContext());
            } else {
                successfulSyncPar = true;
            }

            Cursor res_rs = myDbRs.getAllData();
            if(res_rs.getCount()==0){
                successfulSyncRs = myDbRs.getInitialServerData(getApplicationContext());
            } else {
                successfulSyncRs = true;
            }

            Cursor res_sp = myDbSp.getAllData();
            if(res_sp.getCount()==0){
                successfulSyncSp = myDbSp.getInitialServerData(getApplicationContext());
            } else {
                successfulSyncSp = true;
            }

            Cursor res_st = myDbSt.getAllData();
            if(res_st.getCount()==0){
                successfulSyncSt = myDbSt.getInitialServerData(getApplicationContext());
            } else {
                successfulSyncSt = true;
            }

            if (successfulSyncAthl && successfulSyncCon && successfulSyncPar && successfulSyncRs && successfulSyncSp && successfulSyncSt) {
                session.setDBSynced();
                return "Datenbank erfolgreich synchronisiert!";
            } else {
                return "Fehler bei der Datenbanksynchronisation!";
            }
        }

        @Override
        protected void onPostExecute(String response) {

            pDialog.dismiss();
            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
        }
    }

}
