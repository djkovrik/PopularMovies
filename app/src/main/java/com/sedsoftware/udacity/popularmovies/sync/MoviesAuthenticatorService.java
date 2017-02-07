package com.sedsoftware.udacity.popularmovies.sync;


import android.accounts.Account;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class MoviesAuthenticatorService extends Service {

    private static final String ACCOUNT_TYPE = "com.sedsoftware.udacity";
    private static final String ACCOUNT_NAME = "sync";

    private MoviesAuthenticator mAuthenticator;

    public static Account getAccount() {
        return new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAuthenticator = new MoviesAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
