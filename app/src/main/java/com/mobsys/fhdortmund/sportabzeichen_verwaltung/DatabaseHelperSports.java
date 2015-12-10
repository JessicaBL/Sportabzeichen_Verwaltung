package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelperSports extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "sports.db";
    public static final String TABLE_NAME = "sports_table";
    public static final String COL_1 = "ID";
    public static final String COL_2= "CATEGORY";
    public static final String COL_3= "SPORT";
    public static final String COL_4= "UNIT";



    public DatabaseHelperSports(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,CATEGORY TEXT,SPORT TEXT, UNIT TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST" + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String category, String sport, String unit){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, category);
        contentValues.put(COL_3, sport);
        contentValues.put(COL_4, unit);
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

    public boolean updateData(String id, String category, String sport, String unit){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, category);
        contentValues.put(COL_3, sport);
        contentValues.put(COL_4, unit);
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

    public Cursor selectLastData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select max(id) from" + TABLE_NAME +"", null);
        return res;
    }

    public Cursor selectSingleCategory(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME+ " WHERE CATEGORY ="+id+"", null);
        return res;
    }



}

