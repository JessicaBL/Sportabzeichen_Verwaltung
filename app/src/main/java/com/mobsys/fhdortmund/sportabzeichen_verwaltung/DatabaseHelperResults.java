package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
        db.execSQL("create table " + TABLE_NAME + " (ID TEXT PRIMARY KEY, ID_PRUEFER INTEGER, ID_ATHLETE INTEGER, ID_SPORTS INTEGER, RESULT REAL, RESULT_DATE TEXT, SERVER_SYNCED INTEGER )");
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
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " WHERE id ='" + id + "'", null);
        return res;
    }

    //Gibt die Daten eines Athleten zurück
    public Cursor selectSingleDataAthlete(String id_athlete){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " WHERE id_athlete ='" + id_athlete + "'", null);
        return res;
    }

    public Cursor selectSingleDataSports(String id_sports){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " WHERE id_sports ="+id_sports+"", null);
        return res;
    }

    public Cursor getSyncRows() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE server_synced=0", null);
        return res;
    }

    public boolean getInitialServerData(String ip_port){

        JSONParser jsonParser = new JSONParser();
        JSONArray results;

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

    //Sync data between local and remote db
    public boolean syncData(String ip_port) {

        JSONParser jsonParser = new JSONParser();
        JSONArray results;

        String URL = "http://"+ip_port+"/sportabzeichen/results.php";
        String REQUEST_RESULTS = "results";
        String REQUEST_RESULTS_ID = "id";
        String REQUEST_RESULTS_ID_PRUEFER = "id_pruefer";
        String REQUEST_RESULTS_ID_ATHLETE = "id_athlete";
        String REQUEST_RESULTS_ID_SPORTS = "id_sports";
        String REQUEST_RESULTS_RESULT = "result";
        String REQUEST_RESULTS_RESULT_DATE = "result_date";
        String REQUEST_SUCCESS = "success";

        boolean successSyncLocalToServer = false;
        boolean success = false;

        try {
            Cursor res = getSyncRows();

            Log.d("Sync results db", "Start syncing dbs - sync local to remote");

            if (res.getCount() <= 0) {
                successSyncLocalToServer = true;
            } else {
                JSONArray json = new JSONArray();
                //Sync local unsynced rows with remote db
                while (res.moveToNext()) {

                    JSONObject data = new JSONObject();

                    data.put(REQUEST_RESULTS_ID, res.getString(0));
                    data.put(REQUEST_RESULTS_ID_PRUEFER, res.getString(1));
                    data.put(REQUEST_RESULTS_ID_ATHLETE, res.getString(2));
                    data.put(REQUEST_RESULTS_ID_SPORTS, res.getString(3));
                    data.put(REQUEST_RESULTS_RESULT, res.getString(4));
                    data.put(REQUEST_RESULTS_RESULT_DATE, res.getString(5));

                    json.put(data);

                }

                JSONObject jsonReq = jsonParser.postJSON(URL, json);

                int reqSuccess = jsonReq.getInt(REQUEST_SUCCESS);

                if (reqSuccess == 1) {
                    successSyncLocalToServer = true;
                } else {
                    Log.d("Sync results db", "Failed to sync results data from local db to server, " + json.toString());
                }
            }

            if (!successSyncLocalToServer) {
                return success;
            } else {

                Log.d("Sync results db", "Sync remote to local");
                Cursor resAll = getAllData();

                JSONObject json = jsonParser.getJSONFromUrl(URL);
                results = json.getJSONArray(REQUEST_RESULTS);

                Log.d("Sync results db", "Deleting rows..");
                //Find deleted rows on server and delete them on local db
                while (resAll.moveToNext()) {
                    String id = resAll.getString(0);

                    boolean exist = false;

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject result = results.getJSONObject(i);

                        String idServer = result.getString(REQUEST_RESULTS_ID);

                        if (id.equals(idServer)) {
                            exist = true;
                        }
                    }

                    if (!exist) {
                        boolean deleted = deleteData(id);

                        if (!deleted) {
                            Log.d("Sync results db", "Something went wrong during deleting results..");
                            successSyncLocalToServer = false;
                            break;
                        }
                    }
                }

                if (!successSyncLocalToServer) {
                    return success;
                    //Sync remote rows with local rows
                } else {
                    Log.d("Sync results db", "Insert rows...");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject result = results.getJSONObject(i);

                        String resultId = result.getString(REQUEST_RESULTS_ID);

                        Cursor resRs = selectSingleData(resultId);

                        //Copy remote rows to local db if rows with specified id don't exist on local db
                        if (!resRs.moveToFirst()) {

                            String id_pruefer = result.getString(REQUEST_RESULTS_ID_PRUEFER);
                            String id_athlete = result.getString(REQUEST_RESULTS_ID_ATHLETE);
                            String id_sports = result.getString(REQUEST_RESULTS_ID_SPORTS);
                            String resultValue = result.getString(REQUEST_RESULTS_RESULT);
                            String result_date = result.getString(REQUEST_RESULTS_RESULT_DATE);

                            boolean isInserted = insertData(resultId, id_pruefer, id_athlete, id_sports, resultValue, result_date, "1");

                            if (!isInserted) {
                                success = false;
                                Log.d("Sync results db", "Error during inserting new row in local db..");
                                break;
                            } else {
                                success = true;
                            }
                        } else {
                            success = true;
                        }
                    }

                    return success;

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}

