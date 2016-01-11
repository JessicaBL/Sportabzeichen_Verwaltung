package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

/**
 * Created by bleile on 18.12.2015.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    SessionManager session;
    DatabaseHelper myDb;
    DatabaseHelperCondition myDbCon;
    DatabaseHelperParameter myDbPar;
    DatabaseHelperResults myDbRs;
    DatabaseHelperSports myDbSp;
    DatabaseHelperStation myDbSt;

    private String ip_port= "";

    boolean successfulInitAthl = false;
    boolean successfulInitCon = false;
    boolean successfulInitPar = false;
    boolean successfulInitRs = false;
    boolean successfulInitSp = false;
    boolean successfulInitSt = false;


    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        session = new SessionManager(context);
        myDb = new DatabaseHelper(context);
        myDbCon = new DatabaseHelperCondition(context);
        myDbPar = new DatabaseHelperParameter(context);
        myDbRs = new DatabaseHelperResults(context);
        myDbSp = new DatabaseHelperSports(context);
        myDbSt = new DatabaseHelperStation(context);

        ip_port = context.getString(R.string.ip_port);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);

        session = new SessionManager(context);
        myDb = new DatabaseHelper(context);
        myDbCon = new DatabaseHelperCondition(context);
        myDbPar = new DatabaseHelperParameter(context);
        myDbRs = new DatabaseHelperResults(context);
        myDbSp = new DatabaseHelperSports(context);
        myDbSt = new DatabaseHelperStation(context);

        ip_port = context.getString(R.string.ip_port);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        boolean dbLoaded = session.getDBLoaded();

        //Sync local with remote db
        if(dbLoaded) {

            myDb.syncData(ip_port);
            myDbRs.syncData(ip_port);
            myDbSt.syncData(ip_port);

            //If dbloaded is false or not set in SessionManager load all neccessay dbs from server to local db
        } else {

            if(myDb.getAllData().getCount()==0){
                successfulInitAthl = myDb.getInitialServerData(ip_port);
            } else {
                boolean rowsdeleted = myDb.deleteAllData();
                if (rowsdeleted) {
                    successfulInitAthl = myDb.getInitialServerData(ip_port);
                }
            }

            if(myDbCon.getAllData().getCount()==0){
                successfulInitCon = myDbCon.getInitialServerData(ip_port);
            } else {
                boolean rowsdeleted = myDbCon.deleteAllData();
                if (rowsdeleted) {
                    successfulInitCon = myDbCon.getInitialServerData(ip_port);
                }
            }

            if(myDbPar.getAllData().getCount()==0){
                successfulInitPar = myDbPar.getInitialServerData(ip_port);
            } else {
                boolean rowsdeleted = myDbPar.deleteAllData();
                if(rowsdeleted) {
                    successfulInitPar = myDbPar.getInitialServerData(ip_port);
                }
            }

            if(myDbRs.getAllData().getCount()==0){
                successfulInitRs = myDbRs.getInitialServerData(ip_port);
            } else {
                boolean rowsdeleted = myDbRs.deleteAllData();
                if (rowsdeleted) {
                    successfulInitRs = myDbRs.getInitialServerData(ip_port);
                }
            }

            if(myDbSp.getAllData().getCount()==0){
                successfulInitSp = myDbSp.getInitialServerData(ip_port);
            } else {
                boolean rowsdeleted = myDbSp.deleteAllData();
                if (rowsdeleted) {
                    successfulInitSp = myDbSp.getInitialServerData(ip_port);
                }
            }

            if(myDbSt.getAllData().getCount()==0){
                successfulInitSt = myDbSt.getInitialServerData(ip_port);
            } else {
                boolean rowsdeleted = myDbSt.deleteAllData();
                if (rowsdeleted) {
                    successfulInitSt = myDbSt.getInitialServerData(ip_port);
                }
            }

            if (successfulInitAthl && successfulInitCon && successfulInitPar && successfulInitRs && successfulInitSp && successfulInitSt) {
                session.setDBLoaded();
            }
        }
    }
}
