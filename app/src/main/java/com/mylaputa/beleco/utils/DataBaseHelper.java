package com.mylaputa.beleco.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mylaputa.beleco.utils.Preferences.Preference;

public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(Context context, String name, int version) {
        super(context, name, null, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("CREATE TABLE preferences (" + Preference.WALLPAPER
                + " INTEGER, " + Preference.OFFSET_RANGE + " INTEGER, "
                + Preference.DELAY + " INTEGER, " + Preference.SCROLL_MODE
                + " INTEGER, " + Preference.SHOW_IN_LAUNCHER + " INTEGER);");
        db.execSQL("INSERT INTO preferences (" + Preference.WALLPAPER + ", "
                + Preference.OFFSET_RANGE + ", " + Preference.DELAY + ", "
                + Preference.SCROLL_MODE + ", " + Preference.SHOW_IN_LAUNCHER
                + ") VALUES (2, 2, 2, 1, 1);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

}
