package com.example.blake.nounsonaphone.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Blake on 6/14/2017.
 */

public class WordDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "words.db";
    public static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + WordContract.WordEntry.TABLE_NAME + " (" +
                    WordContract.WordEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    WordContract.WordEntry.COLUMN_WORD + " TEXT NOT NULL)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + WordContract.WordEntry.TABLE_NAME;

    public WordDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Discards all data and starts over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
