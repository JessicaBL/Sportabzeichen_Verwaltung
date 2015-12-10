package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Login extends AppCompatActivity {

    DatabaseHelperPruefer myDbPr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myDbPr = new DatabaseHelperPruefer(this);
        Cursor res = myDbPr.getAllData();

        //Alle Einträge wieder auf inaktiv setzen
        while(res.moveToNext()){
            String id=res.getString(0);
            String fullname=res.getString(1);
            String pw = res.getString(2);

            myDbPr.updateData(id, fullname, pw, "0");
        }


        populateListView();

    }

    public void populateListView() {
        Cursor res = myDbPr.getAllData();

        ArrayList<String> AthletesList = new ArrayList<String>();

        while(res.moveToNext()){
            String id=res.getString(0);
            String name=res.getString(1);
            String password=res.getString(2);
            AthletesList.add(id+ " Name: "+name+", Password: "+password);
        }
        ListAdapter adapter = new ArrayAdapter<>(Login.this, android.R.layout.simple_list_item_1,AthletesList);

        final ListView lv = (ListView)findViewById(R.id.listView_pruefer);
        lv.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_login) {
            EditText name = (EditText)findViewById(R.id.editText_pruefer_name);
            EditText password = (EditText)findViewById(R.id.editText_pruefer_password);
            Cursor res = myDbPr.checkUser(name.getText().toString(), password.getText().toString());
            if (res.getCount() > 0) {
                res.moveToFirst();
                String id_string=res.getString(0);
                String fullname=res.getString(1);
                String pw = res.getString(2);

                //Den aktuellen Nutzer auf aktiv setzen
                myDbPr.updateData(id_string, fullname, pw, "1");

                Toast.makeText(Login.this, "Login erfolgreich", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
            else{
                Toast.makeText(Login.this, "Benutzername oder Passwort falsch", Toast.LENGTH_LONG).show();
            }


        }
        if(id==R.id.action_new_user){
            Intent intent = new Intent(Login.this, NewUser.class);
            startActivity(intent);
        }

        if(id==R.id.action_close_app){
            finish();
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
