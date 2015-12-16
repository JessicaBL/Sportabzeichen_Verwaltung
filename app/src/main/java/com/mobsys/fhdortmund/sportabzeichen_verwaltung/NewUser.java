package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


public class NewUser extends AppCompatActivity {

    DatabaseHelperPruefer myDbPr;
    EditText name, password, repeatPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        myDbPr = new DatabaseHelperPruefer(this);
        name = (EditText)findViewById(R.id.editText_add_name);
        password = (EditText)findViewById(R.id.editText_add_password);
        repeatPassword = (EditText)findViewById(R.id.editText_repeat_password);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_user, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_pruefer) {

            if(name.getText().toString().equals("")){
                Toast.makeText(NewUser.this, "Bitte Benutzernamen angeben", Toast.LENGTH_LONG).show();
            }
            else if(password.getText().toString().equals("")){
                Toast.makeText(NewUser.this, "Bitte Passwort angeben", Toast.LENGTH_LONG).show();
            }
            else if(repeatPassword.getText().toString().equals("")){
                Toast.makeText(NewUser.this, "Bitte das Passwort noch mal angeben", Toast.LENGTH_LONG).show();
            }
            else if(!password.getText().toString().equals(repeatPassword.getText().toString())){
                Toast.makeText(NewUser.this, "Die Passwörter stimmen nicht überein", Toast.LENGTH_LONG).show();
            }
            else{
                boolean isInserted = myDbPr.insertData(name.getText().toString(), password.getText().toString(), "0");
                if(isInserted == true) {
                    Toast.makeText(NewUser.this, "Prüfer angelegt", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(NewUser.this, Login.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(NewUser.this, "Prüfer nicht angelegt", Toast.LENGTH_LONG).show();
                }
            }

            }

        if (id==android.R.id.home){

            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            this.finish();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
