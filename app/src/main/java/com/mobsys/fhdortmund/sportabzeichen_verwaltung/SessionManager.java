package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


/**
 * Created by bleile on 16.12.2015.
 * Manage all session variables from the SharedPreferences class
 */
public class SessionManager {

    SharedPreferences pref;
    Editor editor;
    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "DSAAppPref";
    private static final String DATABASE_LOADED = "DBLoaded";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setDBLoaded() {
        editor.putBoolean(DATABASE_LOADED, true);
        editor.commit();
    }

    public void createLoginSession(String id, String name){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_NAME, name);
        editor.commit();
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();

        Intent login = new Intent(_context, Login.class);
        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(login);
    }

    public boolean getDBLoaded() {
        boolean dbLoaded = pref.getBoolean(DATABASE_LOADED, false);

        return dbLoaded;
    }

    public String getUserId(){
        String id = pref.getString(KEY_ID, null);

        return id;
    }

    public String getUserName() {
        String name = pref.getString(KEY_NAME, null);

        return name;
    }

    public boolean isLoggedIn(){
        boolean isLoggedIn = pref.getBoolean(IS_LOGIN, false);

        return isLoggedIn;
    }

    public void checkLogin(){

        if(!this.isLoggedIn()){
            Intent login = new Intent(_context, Login.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(login);
        }
    }
}
