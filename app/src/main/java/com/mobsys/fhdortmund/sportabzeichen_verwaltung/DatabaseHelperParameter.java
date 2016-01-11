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
public class DatabaseHelperParameter extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "parameter.db";
    public static final String TABLE_NAME = "parameter_table";
    public static final String COL_1 = "ID";
    public static final String COL_2= "ID_SPORTS";
    public static final String COL_3= "ID_CONDITION";
    public static final String COL_4= "PARAMETER";


    public DatabaseHelperParameter(Context context) {

        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY,ID_SPORTS INTEGER,ID_CONDITION INTEGER, PARAMETER TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST" + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String id, String id_sports, String id_condition, String parameter){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, id_sports);
        contentValues.put(COL_3, id_condition);
        contentValues.put(COL_4, parameter);
        long  result = db.insert(TABLE_NAME, null, contentValues);
        if(result==-1)
            return false;
        else
            return true;
    }

    public boolean updateData(String id, String id_sports, String id_condition, String parameter){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, id_sports);
        contentValues.put(COL_3, id_condition);
        contentValues.put(COL_4, parameter);
        return db.update(TABLE_NAME, contentValues, "ID = ?", new String[] { id }) > 0 ;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME, null);
        return res;
    }

    public boolean deleteData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[]{id}) > 0;
    }

    public boolean deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "1", null) > 0;
    }

    public Cursor selectSingleParameter(String id_sports, String id_condtition) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT PARAMETER FROM " + TABLE_NAME + " WHERE ID_SPORTS =" + id_sports + " AND ID_CONDITION =" + id_condtition + "", null);
        return res;
    }

        public boolean getInitialServerData(String ip_port){

        JSONParser jsonParser = new JSONParser();
        JSONArray parameters;

        String URL = "http://"+ip_port+"/sportabzeichen/parameter.php";
        String RESPONSE_PARAMETERS = "parameters";
        String RESPONSE_PARAMETERS_ID = "id";
        String RESPONSE_PARAMETERS_ID_SPORTS = "id_sports";
        String RESPONSE_PARAMETERS_ID_CONDITION = "id_condition";
        String RESPONSE_PARAMETERS_PARAMETER = "parameter";

        boolean success = false;

        JSONObject json = jsonParser.getJSONFromUrl(URL);

        try {

            parameters = json.getJSONArray(RESPONSE_PARAMETERS);

            for (int i=0; i<parameters.length(); i++) {

                JSONObject parameter = parameters.getJSONObject(i);

                String id = parameter.getString(RESPONSE_PARAMETERS_ID);
                String id_sports = parameter.getString(RESPONSE_PARAMETERS_ID_SPORTS);
                String id_condition = parameter.getString(RESPONSE_PARAMETERS_ID_CONDITION);
                String parameterValue = parameter.getString(RESPONSE_PARAMETERS_PARAMETER);

                boolean isInserted = insertData(id, id_sports, id_condition, parameterValue);

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
