package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelperStation extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "station.db";
    public static final String TABLE_NAME = "station_table";
    public static final String COL_1 = "ID";
    public static final String COL_2= "NAME";
    public static final String COL_3= "POSITION_LAT";
    public static final String COL_4= "POSITION_LNG";
    public static final String COL_5= "SPORTS";


    public DatabaseHelperStation(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT, POSITION_LAT TEXT, POSITION_LNG TEXT, SPORTS TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST" + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String name, String position_lat, String position_lng, String sports){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, position_lat);
        contentValues.put(COL_4, position_lng);
        contentValues.put(COL_5, sports);

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

    public boolean updateData(String id, String name, String position_lat, String position_lng, String sports){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, position_lat);
        contentValues.put(COL_4, position_lng);
        contentValues.put(COL_5, sports);
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

    public Cursor selectSingleCategory(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME+ " WHERE CATEGORY ="+id+"", null);
        return res;
    }



}

