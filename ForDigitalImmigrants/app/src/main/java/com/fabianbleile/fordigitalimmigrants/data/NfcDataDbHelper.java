package com.fabianbleile.fordigitalimmigrants.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Fabian on 07.03.2018.
 */

public class NfcDataDbHelper extends SQLiteOpenHelper {
    // The name of the database
    private static final String DATABASE_NAME = "nfcData.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 1;


    // Constructor
    NfcDataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    /**
     * Called when the tasks database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create tasks table (careful to follow SQL formatting rules)
        final String CREATE_TABLE = "CREATE TABLE "  + NfcDataContract.NfcDataEntry.TABLE_NAME + " (" +
                NfcDataContract.NfcDataEntry._ID                + " INTEGER PRIMARY KEY, " +
                NfcDataContract.NfcDataEntry.COLUMN_NFCDATA_FIRSTNAME + " TEXT, " +
                NfcDataContract.NfcDataEntry.COLUMN_NFCDATA_LASTNAME  + " TEXT, " +
                NfcDataContract.NfcDataEntry.COLUMN_NFCDATA_PHONENUMBER  + " TEXT, " +
                NfcDataContract.NfcDataEntry.COLUMN_NFCDATA_EMAIL  + " TEXT, " +
                NfcDataContract.NfcDataEntry.COLUMN_NFCDATA_INSTAGRAM  + " TEXT, " +
                NfcDataContract.NfcDataEntry.COLUMN_NFCDATA_FACEBOOK  + " TEXT, " +
                NfcDataContract.NfcDataEntry.COLUMN_NFCDATA_SNAPCHAT  + " TEXT" +
                ");";

        db.execSQL(CREATE_TABLE);
    }


    /**
     * This method discards the old table of data and calls onCreate to recreate a new one.
     * This only occurs when the version number for this database (DATABASE_VERSION) is incremented.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NfcDataContract.NfcDataEntry.TABLE_NAME);
        onCreate(db);
    }
}
