package com.mii.android.model;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by mii on 2018/3/25.
 */

public class Database {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

}