package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;


public class AthleteOverview extends AppCompatActivity {

    DatabaseHelper myDb;
    EditText editName, editSurname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_overview);

        myDb = new DatabaseHelper(this);


        editName = (EditText) findViewById(R.id.editText_name);
        editSurname = (EditText) findViewById(R.id.editText_surname);
        populateListView();


    }

    private void setSupportActionBar(Toolbar myToolbar) {
    }


    //ListView mit SQLite-Daten erstellen
    public void populateListView(){
        Cursor res = myDb.getAllData();

        ArrayList<String> AthletesList = new ArrayList<String>();

        while(res.moveToNext()){
            String id=res.getString(0);
            String name=res.getString(1);
            String surname=res.getString(2);
            String birthday=res.getString(3);
            String sex=res.getString(4);
            AthletesList.add(id+ " Vorname: "+name+", Nachname: "+surname+", Geburtstag: "+birthday+", Geschlecht: "+sex);
        }
        ListAdapter adapter = new ArrayAdapter<>(AthleteOverview.this, android.R.layout.simple_list_item_1,AthletesList);

        final ListView lv = (ListView)findViewById(R.id.listView_athletes);
        lv.setAdapter(adapter);
        registerForContextMenu(lv);

    }

    //Kontextmenü für Sportler-Eigenschaften (Löschen, ...) erstellen
    @Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
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
                lv.deferNotifyDataSetChanged();

            case R.id.results_athlete:
                Intent intent = new Intent(this, ResultsAthlete.class);
                intent.putExtra("id_athlete", id_athlete);
                startActivity(intent);
        }
        return true;
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


    public void showNewAthlete(View view) {
        Intent intent = new Intent(this, NewAthlete.class);

        startActivity(intent);
    }

    public void refreshAthletes(View view){

        recreate();
    }

}
