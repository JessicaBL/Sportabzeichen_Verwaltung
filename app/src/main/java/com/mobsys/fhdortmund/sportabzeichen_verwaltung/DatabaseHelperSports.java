package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseHelperSports extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "sports.db";
    public static final String TABLE_NAME = "sports_table";
    public static final String COL_1 = "ID";
    public static final String COL_2= "CATEGORY";
    public static final String COL_3= "SPORT";
    public static final String COL_4= "UNIT";
    public static final String COL_5= "PARAMETER";



    public DatabaseHelperSports(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY,CATEGORY TEXT,SPORT TEXT, UNIT TEXT, PARAMETER INTEGER ) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST" + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String id, String category, String sport, String unit, String parameter){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, category);
        contentValues.put(COL_3, sport);
        contentValues.put(COL_4, unit);
        contentValues.put(COL_5, parameter);
        long  result = db.insert(TABLE_NAME, null, contentValues);
        if(result!= -1)
            return true;
        else
            return false;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME, null);
        return res;
    }

    public boolean updateData(String id, String category, String sport, String unit, String parameter){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, category);
        contentValues.put(COL_3, sport);
        contentValues.put(COL_4, unit);
        contentValues.put(COL_5, parameter);
        return db.update(TABLE_NAME, contentValues, "ID = ?", new String[] { id }) > 0 ;
    }

    public boolean deleteData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[]{id}) > 0;
    }

    public boolean deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "1", null) > 0;
    }

    public Cursor selectSingleData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " WHERE ID =" + id + "", null);
        return res;
    }


    public Cursor selectSingleCategory(String category){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME+ " WHERE CATEGORY = '"+category+"'", null);
        return res;
    }

    public Cursor selectSingleSports(String sports){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " WHERE SPORT ='"+sports+"'", null);
        return res;
    }

    public Cursor selectIDFromCategorySports(String category, String sports){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " WHERE CATEGORY ='"+category+"' AND SPORT ='"+sports+"'", null);
        return res;
    }

    public Cursor selectSingleParameterValue(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT PARAMETER FROM " + TABLE_NAME + " WHERE ID ="+id+"", null);
        return res;
    }

    public boolean getInitialServerData(String ip_port){

        JSONParser jsonParser = new JSONParser();
        JSONArray sports;

        String URL = "http://"+ ip_port +"/sportabzeichen/sports.php";
        String RESPONSE_SPORTS = "sports";
        String RESPONSE_SPORTS_ID = "id";
        String RESPONSE_SPORTS_CATEGORY = "category";
        String RESPONSE_SPORTS_SPORT = "sport";
        String RESPONSE_SPORTS_UNIT = "unit";
        String RESPONSE_SPORTS_PARAMETER = "parameter";

        boolean success = false;

        JSONObject json = jsonParser.getJSONFromUrl(URL);

        try {

            sports = json.getJSONArray(RESPONSE_SPORTS);

            for (int i=0; i<sports.length(); i++) {

                JSONObject sport = sports.getJSONObject(i);

                String id = sport.getString(RESPONSE_SPORTS_ID);
                String category = sport.getString(RESPONSE_SPORTS_CATEGORY);
                String sportValue = sport.getString(RESPONSE_SPORTS_SPORT);
                String unit = sport.getString(RESPONSE_SPORTS_UNIT);
                String parameter = sport.getString(RESPONSE_SPORTS_PARAMETER);

                boolean isInserted = insertData(id, category, sportValue, unit, parameter);

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

