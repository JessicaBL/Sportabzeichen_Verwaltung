package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseHelperStation extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "station.db";
    public static final String TABLE_NAME = "station_table";
    public static final String COL_1 = "ID";
    public static final String COL_2= "NAME";
    public static final String COL_3= "POSITION_LAT";
    public static final String COL_4= "POSITION_LNG";
    public static final String COL_5= "SPORTS";
    public static final String COL_6= "LAST_CHANGE";
    public static final String COL_7= "SERVER_SYNCED";


    public DatabaseHelperStation(Context context) {

        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY,NAME TEXT, POSITION_LAT REAL, POSITION_LNG REAL, SPORTS TEXT, LAST_CHANGE TEXT, SERVER_SYNCED INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST" + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String id, String name, String position_lat, String position_lng, String sports, String last_change,String server_synced){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, position_lat);
        contentValues.put(COL_4, position_lng);
        contentValues.put(COL_5, sports);
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

    public boolean updateData(String id, String name, String position_lat, String position_lng, String sports, String last_change, String server_synced){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, position_lat);
        contentValues.put(COL_4, position_lng);
        contentValues.put(COL_5, sports);
        contentValues.put(COL_6, last_change);
        contentValues.put(COL_7, server_synced);
        db.update(TABLE_NAME, contentValues, "ID = ?", new String[] { id });
        return true;
    }

    public Integer deleteData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[]{id});
    }

    public Cursor selectSingleData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " WHERE ID =" + id + "", null);
        return res;
    }

    public boolean getInitialServerData(Context context){

        JSONParser jsonParser = new JSONParser();
        JSONArray stations;

        String ip_port = context.getString(R.string.ip_port);

        String URL = "http://" + ip_port + "/sportabzeichen/stations.php";
        String RESPONSE_STATIONS = "stations";
        String RESPONSE_STATIONS_ID = "id";
        String RESPONSE_STATIONS_NAME = "name";
        String RESPONSE_STATIONS_LAT = "position_lat";
        String RESPONSE_STATIONS_LNG = "position_lng";
        String RESPONSE_STATIONS_IDS_SPORTS = "ids_sports";
        String RESPONSE_STATIONS_LAST_CHANGE = "last_change";

        boolean success = false;

        JSONObject json = jsonParser.getJSONFromUrl(URL);

        try {

            stations = json.getJSONArray(RESPONSE_STATIONS);

            for (int i=0; i<stations.length(); i++) {

                JSONObject station = stations.getJSONObject(i);

                String id = station.getString(RESPONSE_STATIONS_ID);
                String name = station.getString(RESPONSE_STATIONS_NAME);
                String lat = station.getString(RESPONSE_STATIONS_LAT);
                String lng = station.getString(RESPONSE_STATIONS_LNG);
                String ids_sports = station.getString(RESPONSE_STATIONS_IDS_SPORTS);
                String last_change = station.getString(RESPONSE_STATIONS_LAST_CHANGE);

                boolean isInserted = insertData(id, name, lat, lng, ids_sports, last_change, "1");

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

