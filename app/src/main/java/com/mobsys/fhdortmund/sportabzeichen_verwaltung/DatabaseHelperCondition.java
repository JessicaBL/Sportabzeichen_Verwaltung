package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bleile on 14.12.2015.
 */
public class DatabaseHelperCondition extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "condition.db";
    public static final String TABLE_NAME = "condition_table";
    public static final String COL_1 = "ID";
    public static final String COL_2= "SEX";
    public static final String COL_3= "MIN_AGE";
    public static final String COL_4= "MAX_AGE";

    public DatabaseHelperCondition(Context context) {

        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY,SEX TEXT,MIN_AGE INTEGER, MAX_AGE INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST" + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String id, String sex, String min_age, String max_age){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, sex);
        contentValues.put(COL_3, min_age);
        contentValues.put(COL_4, max_age);
        long  result = db.insert(TABLE_NAME, null, contentValues);
        if(result==-1)
            return false;
        else
            return true;
    }

    public boolean updateData(String id, String sex, String min_age, String max_age){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, sex);
        contentValues.put(COL_3, min_age);
        contentValues.put(COL_4, max_age);
        db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{id});
        return true;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME, null);
        return res;
    }

    public Integer deleteData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[]{id});
    }

    public Cursor selectSingleData(int age){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select ID from " + TABLE_NAME + " WHERE " + age + " BETWEEN MIN_AGE AND MAX_AGE", null);
        return res;
    }

    public boolean getInitialServerData(Context context){

        JSONParser jsonParser = new JSONParser();
        JSONArray conditions;

        String ip_port = context.getString(R.string.ip_port);

        String URL = "http://"+ip_port+"/sportabzeichen/conditions.php";
        String RESPONSE_CONDITIONS = "conditions";
        String RESPONSE_CONDITIONS_ID = "id";
        String RESPONSE_CONDITIONS_SEX = "sex";
        String RESPONSE_CONDITIONS_MIN_AGE = "min_age";
        String RESPONSE_CONDITIONS_MAX_AGE = "max_age";

        boolean success = false;

        JSONObject json = jsonParser.getJSONFromUrl(URL);

        try {

            conditions = json.getJSONArray(RESPONSE_CONDITIONS);

            for (int i=0; i<conditions.length(); i++) {

                JSONObject condition = conditions.getJSONObject(i);

                String id = condition.getString(RESPONSE_CONDITIONS_ID);
                String sex = condition.getString(RESPONSE_CONDITIONS_SEX);
                String min_age = condition.getString(RESPONSE_CONDITIONS_MIN_AGE);
                String max_age = condition.getString(RESPONSE_CONDITIONS_MAX_AGE);

                boolean isInserted = insertData(id, sex, min_age, max_age);

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
