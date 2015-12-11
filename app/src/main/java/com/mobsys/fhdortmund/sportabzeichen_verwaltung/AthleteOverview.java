package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AthleteOverview extends AppCompatActivity {

    DatabaseHelper myDb;
    EditText editName, editSurname;
    ArrayList<String> AthletesList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_overview);

        myDb = new DatabaseHelper(this);

        editName = (EditText) findViewById(R.id.editText_name);
        editSurname = (EditText) findViewById(R.id.editText_surname);
        ListView lv = (ListView)findViewById(R.id.listView_athletes);

        populateListView(lv, AthletesList);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
    }

    //ListView mit SQLite-Daten erstellen
    public void populateListView(ListView lv, ArrayList AthletesList){
        Cursor res = myDb.getAllData();


        while(res.moveToNext()){
            String id=res.getString(0);
            String name=res.getString(1);
            String surname=res.getString(2);
            String birthday=res.getString(3);
            String sex=res.getString(4);
            AthletesList.add(id+ " - "+name+" "+surname+", "+birthday+", "+sex);
        }
        ListAdapter adapter = new ArrayAdapter<>(AthleteOverview.this, android.R.layout.simple_list_item_1,AthletesList);

        lv.setAdapter(adapter);
        registerForContextMenu(lv);

    }

    //Kontextmenü für Sportler-Eigenschaften (Löschen, ...) erstellen
    @Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_athletes_overview, menu);
    }

    @Override public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        final ListView lv = (ListView) findViewById(R.id.listView_athletes);
        String selectedFromList = (lv.getItemAtPosition(info.position).toString());

        String[] splitResult = selectedFromList.split(" ");
        String id_athlete = splitResult[0];
        switch (item.getItemId()) {
            case R.id.delete_athlete:
                myDb.deleteData(id_athlete);
                //   Show message
                Toast.makeText(getApplicationContext(), "Sportler gelöscht", Toast.LENGTH_LONG).show();

            case R.id.results_athlete:
                Intent intent = new Intent(this, ResultsAthlete.class);
                intent.putExtra("id_athlete", id_athlete);
                startActivity(intent);

        }
        return true;
    }

    public void search(final ArrayList AthletesList) {

        ListAdapter adapter = new ArrayAdapter<>(AthleteOverview.this, android.R.layout.simple_list_item_1,AthletesList);
        final ListView lv = (ListView)findViewById(R.id.listView_athletes);
        lv.setAdapter(adapter);
        registerForContextMenu(lv);

        LayoutInflater li = LayoutInflater.from(AthleteOverview.this);

        final View promptsView = li.inflate(R.layout.search_layout, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(AthleteOverview.this);

        final EditText input = (EditText) promptsView.findViewById(R.id.search_edittext);



        alert.setTitle("Nach Sportler suchen");
        alert.setView(promptsView);


        alert.setPositiveButton("Suche", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String result = input.getText().toString();
                ArrayList<String> Original_List = AthletesList;
                ArrayList<String> AthletesList_Search = new ArrayList<String>();

                for (String s : Original_List) {
                    if (s.contains(result) == true) {
                        AthletesList_Search.add(s);
                    }
                }

                if (AthletesList_Search.isEmpty()) {
                    Toast.makeText(AthleteOverview.this, "Keinen Sportler gefunden", Toast.LENGTH_LONG).show();
                    dialog.cancel();
                } else {
                    ListAdapter adapter = new ArrayAdapter<>(AthleteOverview.this, android.R.layout.simple_list_item_1,AthletesList_Search);
                    lv.setAdapter(adapter);
                    registerForContextMenu(lv);

                }
            }

        });
        alert.setNegativeButton("abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.show();

    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch(keycode) {
            case KeyEvent.KEYCODE_BACK:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onKeyDown(keycode, e);
    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_athlete_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_athlete) {
            Intent intent = new Intent(this, NewAthlete.class);
            startActivity(intent);
        }
        if (id == R.id.action_refresh) {
            recreate();
        }

        if(id ==R.id.action_search_ao){
            search(AthletesList);
        }

        if (id==android.R.id.home){

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
                this.finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }



}
