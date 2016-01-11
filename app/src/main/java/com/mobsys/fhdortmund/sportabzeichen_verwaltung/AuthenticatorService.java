package com.mobsys.fhdortmund.sportabzeichen_verwaltung;

import android.accounts.Account;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by bleile on 18.12.2015.
 */
public class AuthenticatorService extends Service {

    private static final String ACCOUNT_NAME="dummyaccount";
    private StubAuthenticator authenticator;


    //Get specified account
    public static Account GetAccount() {
        final String accountName = ACCOUNT_NAME;
        return new Account(accountName,"com.mobsys.fhdortmund.sportabzeichen_verwaltung");
    }

    @Override
    public void onCreate(){

        authenticator = new StubAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent){
        return authenticator.getIBinder();
    }
}
