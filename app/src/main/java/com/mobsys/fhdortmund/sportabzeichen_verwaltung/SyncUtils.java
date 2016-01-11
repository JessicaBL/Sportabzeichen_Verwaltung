package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by bleile on 28.12.2015.
 */
public class SyncUtils {

    private static final long SYNC_FREQUENCY = 60*30;
    private static final String CONTENT_AUTHORITY = "com.mobsys.fhdortmund.sportabzeichen_verwaltung.provider";
    private static final String PREF_NAME = "DSAAppPref";
    private static final String SETUP_COMPLETE = "setupComplete";

    //Initialize sync service
    public static void CreateSyncAccount(Context context) {

        boolean newAccount = false;
        boolean setupComplete = context.getSharedPreferences(PREF_NAME, 0).getBoolean(SETUP_COMPLETE, false);

        //Get account from AuthenticatorService
        Account account = AuthenticatorService.GetAccount();
        //Get system account service
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);


        //Add account to account service and set settings
        if(accountManager.addAccountExplicitly(account, null, null)) {

            ContentResolver.setIsSyncable(account, CONTENT_AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(account, CONTENT_AUTHORITY, true);
            ContentResolver.addPeriodicSync(account, CONTENT_AUTHORITY, new Bundle(), SYNC_FREQUENCY);

            newAccount = true;
        }

        //If intial setup didn't complete trigger sync
        if (newAccount || !setupComplete) {
            TriggerRefresh();
            context.getSharedPreferences(PREF_NAME, 0).edit().putBoolean(SETUP_COMPLETE, true).commit();
        }
    }

    //Trigger new sync task
    public static void TriggerRefresh() {

        Bundle b = new Bundle();

        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        Log.d("Sync task", "Trigger sync..");
        ContentResolver.requestSync(AuthenticatorService.GetAccount(), CONTENT_AUTHORITY, b);
        Log.d("Sync task", "Sync is done");

    }
}
