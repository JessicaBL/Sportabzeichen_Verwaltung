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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        db.execSQL("create table " + TABLE_NAME + " (ID TEXT PRIMARY KEY,NAME TEXT, POSITION_LAT REAL, POSITION_LNG REAL, SPORTS TEXT, LAST_CHANGE TEXT, SERVER_SYNCED INTEGER)");
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
        return db.update(TABLE_NAME, contentValues, "ID = ?", new String[] { id }) > 0 ;
    }

    public boolean updateSyncedRows() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_7, "1");
        return db.update(TABLE_NAME, contentValues, "server_synced = ?", new String[]{"0"}) > 0;
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
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " WHERE ID ='" + id + "'", null);
        return res;
    }

    public Cursor getSyncRows() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE server_synced=0", null);
        return res;
    }

    public boolean getInitialServerData(String ip_port){

        JSONParser jsonParser = new JSONParser();
        JSONArray stations;

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


    //Sync data between local and remote db
    public boolean syncData(String ip_port) {

        JSONParser jsonParser = new JSONParser();
        JSONArray stations;

        String URL = "http://" + ip_port + "/sportabzeichen/stations.php";
        String REQUEST_STATIONS = "stations";
        String REQUEST_STATIONS_ID = "id";
        String REQUEST_STATIONS_NAME = "name";
        String REQUEST_STATIONS_LAT = "position_lat";
        String REQUEST_STATIONS_LNG = "position_lng";
        String REQUEST_STATIONS_IDS_SPORTS = "ids_sports";
        String REQUEST_STATIONS_LAST_CHANGE = "last_change";
        String REQUEST_SUCCESS = "success";

        boolean successSyncLocalToServer = false;
        boolean success = false;

        try {
            Cursor res = getSyncRows();

            Log.d("Sync stations db", "Start syncing dbs - sync local to remote");

            if (res.getCount() <= 0) {
                successSyncLocalToServer = true;
            } else {
                JSONArray json = new JSONArray();
                //Sync local unsynced rows with remote db
                while (res.moveToNext()) {

                    JSONObject data = new JSONObject();

                    data.put(REQUEST_STATIONS_ID, res.getString(0));
                    data.put(REQUEST_STATIONS_NAME, res.getString(1));
                    data.put(REQUEST_STATIONS_LAT, res.getString(2));
                    data.put(REQUEST_STATIONS_LNG, res.getString(3));
                    data.put(REQUEST_STATIONS_IDS_SPORTS, res.getString(4));
                    data.put(REQUEST_STATIONS_LAST_CHANGE, res.getString(5));

                    json.put(data);

                }

                JSONObject jsonReq = jsonParser.postJSON(URL, json);

                int reqSuccess = jsonReq.getInt(REQUEST_SUCCESS);

                if (reqSuccess == 1) {
                    boolean localDbUpadted = updateSyncedRows();
                    if (localDbUpadted) {
                        successSyncLocalToServer = true;
                    } else {
                        Log.d("Sync stations db: ", "Failed to update local server_synced column " + json.toString());
                    }
                } else {
                    Log.d("Sync stations db", "Failed to sync stations data from local db to server, " + json.toString());
                }
            }

            if (!successSyncLocalToServer) {
                return success;
            } else {

                Log.d("Sync stations db", "Sync remote to local");
                Cursor resAll = getAllData();

                JSONObject json = jsonParser.getJSONFromUrl(URL);
                stations = json.getJSONArray(REQUEST_STATIONS);

                Log.d("Sync stations db", "Deleting rows..");
                //Find deleted rows on server and delete them on local db
                while (resAll.moveToNext()) {
                    String id = resAll.getString(0);

                    boolean exist = false;

                    for (int i = 0; i < stations.length(); i++) {
                        JSONObject station = stations.getJSONObject(i);

                        String idServer = station.getString(REQUEST_STATIONS_ID);

                        if (id.equals(idServer)) {
                            exist = true;
                        }
                    }

                    if (!exist) {
                        boolean deleted = deleteData(id);

                        if (!deleted) {
                            Log.d("Sync stations db", "Something went wrong during deleting stations..");
                            successSyncLocalToServer = false;
                            break;
                        }
                    }
                }

                if (!successSyncLocalToServer) {
                    return success;
                    //Sync remote rows with local rows
                } else {
                    Log.d("Sync stations db", "Insert or update rows...");
                    for (int i = 0; i < stations.length(); i++) {
                        JSONObject station = stations.getJSONObject(i);

                        String stationId = station.getString(REQUEST_STATIONS_ID);

                        Cursor resSt = selectSingleData(stationId);

                        //Copy remote rows to local db if rows with specified id don't exist on local db
                        if (!resSt.moveToFirst()) {

                            String name = station.getString(REQUEST_STATIONS_NAME);
                            String lat = station.getString(REQUEST_STATIONS_LAT);
                            String lng = station.getString(REQUEST_STATIONS_LNG);
                            String ids_sports = station.getString(REQUEST_STATIONS_IDS_SPORTS);
                            String last_change = station.getString(REQUEST_STATIONS_LAST_CHANGE);

                            boolean isInserted = insertData(stationId, name, lat, lng, ids_sports, last_change, "1");

                            if (!isInserted) {
                                success = false;
                                Log.d("Sync stations db", "Error during inserting new row in local db..");
                                break;
                            } else {
                                success = true;
                            }
                            //Check if local timestamp is older than remote timestamp - update local rows with older timestamp
                        } else {


                            String last_changeLocal = resSt.getString(5);
                            String last_changeServer = station.getString(REQUEST_STATIONS_LAST_CHANGE);

                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                            Date localDate = dateFormat.parse(last_changeLocal);
                            Date serverDate = dateFormat.parse(last_changeServer);

                            if (localDate.before(serverDate)) {

                                String name = station.getString(REQUEST_STATIONS_NAME);
                                String lat = station.getString(REQUEST_STATIONS_LAT);
                                String lng = station.getString(REQUEST_STATIONS_LNG);
                                String ids_sports = station.getString(REQUEST_STATIONS_IDS_SPORTS);
                                String last_change = station.getString(REQUEST_STATIONS_LAST_CHANGE);

                                boolean isUpdated = updateData(stationId, name, lat, lng, ids_sports, last_change, "1");

                                if (!isUpdated) {
                                    success = false;
                                    Log.d("Sync stations db", "Error during updating row in local db..");
                                    break;
                                } else {
                                    success = true;
                                }
                            } else {
                                success = true;
                            }
                        }
                    }

                    return success;

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;

    }
}

