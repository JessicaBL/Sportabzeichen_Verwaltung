/**
 * Created by Hendrik on 03.11.2015.
 */

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

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "athlete.db";
    public static final String TABLE_NAME = "athlete_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "SURNAME";
    public static final String COL_4 = "SEX";
    public static final String COL_5 = "BIRTHDAY";
    public static final String COL_6 = "LAST_CHANGE";
    public static final String COL_7 = "SERVER_SYNCED";


    public DatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID TEXT PRIMARY KEY,NAME TEXT,SURNAME TEXT, SEX TEXT, BIRTHDAY TEXT, LAST_CHANGE TEXT, SERVER_SYNCED INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST" + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String id, String name, String surname, String sex, String birthday, String last_change, String server_synced) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, surname);
        contentValues.put(COL_4, sex);
        contentValues.put(COL_5, birthday);
        contentValues.put(COL_6, last_change);
        contentValues.put(COL_7, server_synced);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    public boolean updateData(String id, String name, String surname, String sex, String birthday, String last_change, String server_synced) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, surname);
        contentValues.put(COL_4, sex);
        contentValues.put(COL_5, birthday);
        contentValues.put(COL_6, last_change);
        contentValues.put(COL_7, server_synced);
        return db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{id}) > 0;
    }

    public boolean deleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[]{id}) > 0;
    }

    public boolean deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "1", null) > 0;
    }

    public Cursor selectSingleDataAthlete(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " WHERE id ='" + id + "'", null);
        return res;
    }

    public Cursor getSyncRows() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE server_synced=0", null);
        return res;
    }

    //Setup database initially and copy data from remote server to local server
    public boolean getInitialServerData(String ip_port) {

        JSONParser jsonParser = new JSONParser();
        JSONArray athletes;

        String URL = "http://" + ip_port + "/sportabzeichen/athletes.php";
        String RESPONSE_ATHLETES = "athletes";
        String RESPONSE_ATHLETE_ID = "id";
        String RESPONSE_ATHLETE_NAME = "name";
        String RESPONSE_ATHLETE_SURNAME = "surname";
        String RESPONSE_ATHLETE_SEX = "sex";
        String RESPONSE_ATHLETE_BIRTHDAY = "birthday";
        String RESPONSE_ATHLETE_LAST_CHANGE = "last_change";

        boolean success = false;

        //Get JSON objects from remote server
        JSONObject json = jsonParser.getJSONFromUrl(URL);

        try {

            athletes = json.getJSONArray(RESPONSE_ATHLETES);
            Log.d("Setup athlete db: ", "Starting copying data from remote db");

            //Copy each JSON object in local db
            for (int i = 0; i < athletes.length(); i++) {

                JSONObject athlete = athletes.getJSONObject(i);

                String id = athlete.getString(RESPONSE_ATHLETE_ID);
                String name = athlete.getString(RESPONSE_ATHLETE_NAME);
                String surname = athlete.getString(RESPONSE_ATHLETE_SURNAME);
                String sex = athlete.getString(RESPONSE_ATHLETE_SEX);
                String birthday = athlete.getString(RESPONSE_ATHLETE_BIRTHDAY);
                String last_change = athlete.getString(RESPONSE_ATHLETE_LAST_CHANGE);

                boolean isInserted = insertData(id, name, surname, sex, birthday, last_change, "1");

                if (isInserted) {
                    success = true;
                } else {
                    break;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("Setup athlete db: ", "Setting up db is done - " + success);
        return success;

    }

    //Sync data between local and remote db
    public boolean syncData(String ip_port) {

        JSONParser jsonParser = new JSONParser();
        JSONArray athletes;

        String URL = "http://" + ip_port + "/sportabzeichen/athletes.php";
        String REQUEST_ATHLETES = "athletes";
        String REQUEST_ATHLETE_ID = "id";
        String REQUEST_ATHLETE_NAME = "name";
        String REQUEST_ATHLETE_SURNAME = "surname";
        String REQUEST_ATHLETE_SEX = "sex";
        String REQUEST_ATHLETE_BIRTHDAY = "birthday";
        String REQUEST_ATHLETE_LAST_CHANGE = "last_change";
        String REQUEST_SUCCESS = "success";

        boolean successSyncLocalToServer = false;
        boolean success = false;

        try {
            Cursor res = getSyncRows();

            Log.d("Sync athlete db: ", "Start syncing dbs - sync local to remote");

            if (res.getCount() <= 0) {
                successSyncLocalToServer = true;
            } else {
                JSONArray json = new JSONArray();
                //Sync local unsynced rows with remote db
                while (res.moveToNext()) {

                    JSONObject data = new JSONObject();

                    data.put(REQUEST_ATHLETE_ID, res.getString(0));
                    data.put(REQUEST_ATHLETE_NAME, res.getString(1));
                    data.put(REQUEST_ATHLETE_SURNAME, res.getString(2));
                    data.put(REQUEST_ATHLETE_SEX, res.getString(3));
                    data.put(REQUEST_ATHLETE_BIRTHDAY, res.getString(4));
                    data.put(REQUEST_ATHLETE_LAST_CHANGE, res.getString(5));

                    json.put(data);

                }

                JSONObject jsonReq = jsonParser.postJSON(URL, json);

                int reqSuccess = jsonReq.getInt(REQUEST_SUCCESS);

                if (reqSuccess == 1) {
                    successSyncLocalToServer = true;
                } else {
                    Log.d("Sync athlete db: ", "Failed to sync athlete data from local db to server, " + json.toString());
                }
            }

            if (!successSyncLocalToServer) {
                return success;
            } else {

                Log.d("Sync athlete db: ", "Sync remote to local");
                Cursor resAll = getAllData();

                JSONObject json = jsonParser.getJSONFromUrl(URL);
                athletes = json.getJSONArray(REQUEST_ATHLETES);

                Log.d("Sync athlete db: ", "Deleting rows..");
                //Find deleted rows on server and delete them on local db
                while (resAll.moveToNext()) {
                    String id = resAll.getString(0);

                    boolean exist = false;

                    for (int i = 0; i < athletes.length(); i++) {
                        JSONObject athlete = athletes.getJSONObject(i);

                        String idServer = athlete.getString(REQUEST_ATHLETE_ID);

                        if (id.equals(idServer)) {
                            exist = true;
                        }
                    }

                    if (!exist) {
                        boolean deleted = deleteData(id);

                        if (!deleted) {
                            Log.d("Local db error: ", "Something went wrong during deleting athlete..");
                            successSyncLocalToServer = false;
                            break;
                        }
                    }
                }

                if (!successSyncLocalToServer) {
                    return success;
                    //Sync remote rows with local rows
                } else {
                    Log.d("Sync athlete db: ", "Insert or update rows...");
                    for (int i = 0; i < athletes.length(); i++) {
                        JSONObject athlete = athletes.getJSONObject(i);

                        String athleteId = athlete.getString(REQUEST_ATHLETE_ID);

                        Cursor resAthl = selectSingleDataAthlete(athleteId);

                        //Copy remote rows to local db if rows with specified id don't exist on local db
                        if (!resAthl.moveToFirst()) {

                            String name = athlete.getString(REQUEST_ATHLETE_NAME);
                            String surname = athlete.getString(REQUEST_ATHLETE_SURNAME);
                            String sex = athlete.getString(REQUEST_ATHLETE_SEX);
                            String birthday = athlete.getString(REQUEST_ATHLETE_BIRTHDAY);
                            String last_change = athlete.getString(REQUEST_ATHLETE_LAST_CHANGE);

                            boolean isInserted = insertData(athleteId, name, surname, sex, birthday, last_change, "1");

                            if (!isInserted) {
                                success = false;
                                Log.d("Error db sync: ", "Error during inserting new row in local db..");
                                break;
                            } else {
                                success = true;
                            }
                            //Check if local timestamp is older than remote timestamp - update local rows with older timestamp
                        } else {


                            String last_changeLocal = resAthl.getString(5);
                            String last_changeServer = athlete.getString(REQUEST_ATHLETE_LAST_CHANGE);

                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                            Date localDate = dateFormat.parse(last_changeLocal);
                            Date serverDate = dateFormat.parse(last_changeServer);

                            if (localDate.before(serverDate)) {

                                String name = athlete.getString(REQUEST_ATHLETE_NAME);
                                String surname = athlete.getString(REQUEST_ATHLETE_SURNAME);
                                String sex = athlete.getString(REQUEST_ATHLETE_SEX);
                                String birthday = athlete.getString(REQUEST_ATHLETE_BIRTHDAY);
                                String last_change = athlete.getString(REQUEST_ATHLETE_LAST_CHANGE);

                                boolean isUpdated = updateData(athleteId, name, surname, sex, birthday, last_change, "1");

                                if (!isUpdated) {
                                    success = false;
                                    Log.d("Error db sync: ", "Error during updating row in local db..");
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
