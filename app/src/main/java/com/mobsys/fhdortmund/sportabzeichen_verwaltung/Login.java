package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Login extends AppCompatActivity {

    DatabaseHelperPruefer myDbPr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        myDbPr = new DatabaseHelperPruefer(this);
        myDbPr.insertData("test", "1234");

        populateListView();
    }

    public void populateListView(){
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
        registerForContextMenu(lv);

    }




    public void showMainActivity(View view) {

        EditText name = (EditText)findViewById(R.id.editText_pruefer_name);
        EditText password = (EditText)findViewById(R.id.editText_pruefer_password);
        TextView error = (TextView)findViewById(R.id.label_error);
        Cursor res = myDbPr.checkUser(name.getText().toString(), password.getText().toString());
        if (res.getCount() > 0) {
            Intent intent = new Intent(this, MainActivity.class);

            res.moveToFirst();
            String id_pruefer = res.getString(0);
            intent.putExtra("id_pruefer", id_pruefer);


            startActivity(intent);
        }
        else{
            error.setText("Wrong name or password!");
        }
    }
}
