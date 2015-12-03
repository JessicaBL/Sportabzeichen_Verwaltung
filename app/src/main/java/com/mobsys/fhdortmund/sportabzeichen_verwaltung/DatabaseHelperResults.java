package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelperResults extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "results.db";
    public static final String TABLE_NAME = "results_table";
    public static final String COL_1 = "ID";
    public static final String COL_2= "ID_ATHLETE";
    public static final String COL_3= "ID_SPORTS";
    public static final String COL_4= "RESULT";
    public static final String COL_5= "RESULT_DATE";
    public static final String COL_6= "ID_PRUEFER";



    public DatabaseHelperResults(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,ID_ATHLETE TEXT,ID_SPORTS TEXT, RESULT TEXT, RESULT_DATE TEXT, ID_PRUEFER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST" + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String id_athlete, String id_sports, String result, String result_date, String id_pruefer){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, id_athlete);
        contentValues.put(COL_3, id_sports);
        contentValues.put(COL_4, result);
        contentValues.put(COL_5, result_date);
        contentValues.put(COL_6, id_pruefer);
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

    public boolean updateData(String id, String id_athlete, String id_sports, String result, String result_date, String id_pruefer){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, id_athlete);
        contentValues.put(COL_3, id_sports);
        contentValues.put(COL_4, result);
        contentValues.put(COL_5, result_date);
        contentValues.put(COL_6, id_pruefer);
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
        Cursor res = db.rawQuery("select * from " + TABLE_NAME+ " WHERE id_athlete ="+id_athlete+"", null);
        return res;
    }

    public Cursor selectSingleDataSports(String id_sports){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " WHERE id_sports ="+id_sports+"", null);
        return res;
    }


}

