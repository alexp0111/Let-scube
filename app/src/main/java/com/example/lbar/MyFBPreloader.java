package com.example.lbar;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class MyFBPreloader extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // FIXME: This stroke break like logic if internet is slow [Just remove?]
        // FirebaseDatabase.getInstance(getString(R.string.fdb_inst)).setPersistenceEnabled(true);
    }
}
