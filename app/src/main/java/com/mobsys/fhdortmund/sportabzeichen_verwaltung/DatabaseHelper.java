/**
 * Created by Hendrik on 03.11.2015.
 */

package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "athlete.db";
    public static final String TABLE_NAME = "athlete_table";
    public static final String COL_1 = "ID";
    public static final String COL_2= "NAME";
    public static final String COL_3= "SURNAME";
    public static final String COL_4= "SEX";
    public static final String COL_5= "BIRTHDAY";
    public static final String COL_6= "LAST_CHANGE";
    public static final String COL_7= "SERVER_SYNCED";


    public DatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY,NAME TEXT,SURNAME TEXT, SEX TEXT, BIRTHDAY TEXT, LAST_CHANGE TEXT, SERVER_SYNCED INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST" + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String id, String name, String surname, String sex, String birthday, String last_change, String server_synced){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, surname);
        contentValues.put(COL_4, sex);
        contentValues.put(COL_5, birthday);
        contentValues.put(COL_6, last_change);
        contentValues.put(COL_7, server_synced);
        long  result = db.insert(TABLE_NAME, null, contentValues);
        if(result==-1)
            return false;
        else
            return true;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME, null);
        return res;
    }

    public boolean updateData(String id, String name, String surname, String sex, String birthday, String last_change, String server_synced){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, surname);
        contentValues.put(COL_4, sex);
        contentValues.put(COL_5, birthday);
        contentValues.put(COL_6, last_change);
        contentValues.put(COL_7, server_synced);
        db.update(TABLE_NAME, contentValues, "ID = ?", new String[] { id });
        return true;
    }

    public Integer deleteData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[]{id});
    }

    public Cursor selectSingleDataAthlete(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " WHERE id =" + id + "", null);
        return res;
    }

    public boolean getInitialServerData(Context context){

        JSONParser jsonParser = new JSONParser();
        JSONArray athletes;

        String ip_port = context.getString(R.string.ip_port);

        String URL = "http://"+ip_port+"/sportabzeichen/athletes.php";
        String RESPONSE_ATHLETES = "athletes";
        String RESPONSE_ATHLETE_ID = "id";
        String RESPONSE_ATHLETE_NAME = "name";
        String RESPONSE_ATHLETE_SURNAME = "surname";
        String RESPONSE_ATHLETE_SEX = "sex";
        String RESPONSE_ATHLETE_BIRTHDAY = "birthday";
        String RESPONSE_ATHLETE_LAST_CHANGE = "last_change";

        boolean success = false;

        JSONObject json = jsonParser.getJSONFromUrl(URL);

        try {

            athletes = json.getJSONArray(RESPONSE_ATHLETES);

            for (int i=0; i<athletes.length(); i++) {

                JSONObject athlete = athletes.getJSONObject(i);

                String id = athlete.getString(RESPONSE_ATHLETE_ID);
                String name = athlete.getString(RESPONSE_ATHLETE_NAME);
                String surname = athlete.getString(RESPONSE_ATHLETE_SURNAME);
                String sex = athlete.getString(RESPONSE_ATHLETE_SEX);
                String birthday = athlete.getString(RESPONSE_ATHLETE_BIRTHDAY);
                String last_change = athlete.getString(RESPONSE_ATHLETE_LAST_CHANGE);

                boolean isInserted = insertData(id, name, surname, sex, birthday, last_change, "1");

                if(isInserted){
                    success = true;
                } else {
                    break;
                }
            }

        } catch (JSONException e){
            e.printStackTrace();
        }

        return success;

    }







}
