package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseHelperResults extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "results.db";
    public static final String TABLE_NAME = "results_table";
    public static final String COL_1 = "ID";
    public static final String COL_2= "ID_PRUEFER";
    public static final String COL_3= "ID_ATHLETE";
    public static final String COL_4= "ID_SPORTS";
    public static final String COL_5= "RESULT";
    public static final String COL_6= "RESULT_DATE";
    public static final String COL_7= "SERVER_SYNCED";



    public DatabaseHelperResults(Context context) {

        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY, ID_PRUEFER INTEGER, ID_ATHLETE INTEGER, ID_SPORTS INTEGER, RESULT REAL, RESULT_DATE TEXT, SERVER_SYNCED INTEGER )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST" + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String id,String id_pruefer, String id_athlete, String id_sports, String result, String result_date, String server_synced ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, id_pruefer);
        contentValues.put(COL_3, id_athlete);
        contentValues.put(COL_4, id_sports);
        contentValues.put(COL_5, result);
        contentValues.put(COL_6, result_date);
        contentValues.put(COL_7, server_synced);
        long  result_data = db.insert(TABLE_NAME, null, contentValues);
        if(result_data==-1)
            return false;
        else
            return true;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME, null);
        return res;
    }

    public boolean updateData(String id, String id_athlete,String id_pruefer, String id_sports, String result, String result_date, String server_synced ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, id_pruefer);
        contentValues.put(COL_3, id_athlete);
        contentValues.put(COL_4, id_sports);
        contentValues.put(COL_5, result);
        contentValues.put(COL_6, result_date);
        contentValues.put(COL_7, server_synced);
        db.update(TABLE_NAME, contentValues, "ID = ?", new String[] { id });
        return true;
    }

    public Integer deleteData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[]{id});
    }

    //Gibt die Daten eines Athleten zurück
    public Cursor selectSingleDataAthlete(String id_athlete){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " WHERE id_athlete =" + id_athlete + "", null);
        return res;
    }

    public Cursor selectSingleDataSports(String id_sports){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " WHERE id_sports ="+id_sports+"", null);
        return res;
    }

    public boolean getInitialServerData(Context context){

        JSONParser jsonParser = new JSONParser();
        JSONArray results;

        String ip_port = context.getString(R.string.ip_port);


        String URL = "http://"+ip_port+"/sportabzeichen/results.php";
        String RESPONSE_RESULTS = "results";
        String RESPONSE_RESULTS_ID = "id";
        String RESPONSE_RESULTS_ID_PRUEFER = "id_pruefer";
        String RESPONSE_RESULTS_ID_ATHLETE = "id_athlete";
        String RESPONSE_RESULTS_ID_SPORTS = "id_sports";
        String RESPONSE_RESULTS_RESULT = "result";
        String RESPONSE_RESULTS_RESULT_DATE = "result_date";

        boolean success = false;

        JSONObject json = jsonParser.getJSONFromUrl(URL);

        try {

            results = json.getJSONArray(RESPONSE_RESULTS);

            for (int i=0; i<results.length(); i++) {

                JSONObject result = results.getJSONObject(i);

                String id = result.getString(RESPONSE_RESULTS_ID);
                String id_pruefer = result.getString(RESPONSE_RESULTS_ID_PRUEFER);
                String id_athlete = result.getString(RESPONSE_RESULTS_ID_ATHLETE);
                String id_sports = result.getString(RESPONSE_RESULTS_ID_SPORTS);
                String resultValue = result.getString(RESPONSE_RESULTS_RESULT);
                String result_date = result.getString(RESPONSE_RESULTS_RESULT_DATE);

                boolean isInserted = insertData(id, id_pruefer,id_athlete, id_sports, resultValue, result_date, "1");

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

