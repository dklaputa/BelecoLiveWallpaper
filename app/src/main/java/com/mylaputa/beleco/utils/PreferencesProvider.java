package com.mylaputa.beleco.utils;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.mylaputa.beleco.utils.Preferences.Preference;

public class PreferencesProvider extends ContentProvider {
    public static final int WALLPAPER = 1;
    public static final int OFFSET_RANGE = 2;
    public static final int DELAY = 3;
    public static final int SCROLL_MODE = 4;
    public static final int SHOW_IN_LAUNCHER = 5;
    public static final int ALL = 6;
    private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        matcher.addURI(Preferences.AUTHORITY, Preference.WALLPAPER, WALLPAPER);
        matcher.addURI(Preferences.AUTHORITY, Preference.OFFSET_RANGE,
                OFFSET_RANGE);
        matcher.addURI(Preferences.AUTHORITY, Preference.DELAY, DELAY);
        matcher.addURI(Preferences.AUTHORITY, Preference.SCROLL_MODE,
                SCROLL_MODE);
        matcher.addURI(Preferences.AUTHORITY, Preference.SHOW_IN_LAUNCHER,
                SHOW_IN_LAUNCHER);
        matcher.addURI(Preferences.AUTHORITY, Preference.ALL, ALL);
    }

    private DataBaseHelper mDataBaseHelper;

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
        mDataBaseHelper = new DataBaseHelper(getContext(), "beleco", 1);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = mDataBaseHelper.getReadableDatabase();
        switch (matcher.match(uri)) {
            case WALLPAPER:
                return db.rawQuery("select " + Preference.WALLPAPER
                        + " from preferences", null);
            case OFFSET_RANGE:
                return db.rawQuery("select " + Preference.OFFSET_RANGE
                        + " from preferences", null);
            case DELAY:
                return db.rawQuery("select " + Preference.DELAY
                        + " from preferences", null);
            case SCROLL_MODE:
                return db.rawQuery("select " + Preference.SCROLL_MODE
                        + " from preferences", null);
            case SHOW_IN_LAUNCHER:
                return db.rawQuery("select " + Preference.SHOW_IN_LAUNCHER
                        + " from preferences", null);
            case ALL:
                return db.rawQuery("select " + Preference.WALLPAPER + ", "
                        + Preference.OFFSET_RANGE + ", " + Preference.DELAY + ", "
                        + Preference.SCROLL_MODE + ", "
                        + Preference.SHOW_IN_LAUNCHER + " from preferences", null);
            default:
                return null;
        }
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = mDataBaseHelper.getWritableDatabase();
        int num = 0;
        switch (matcher.match(uri)) {
            case WALLPAPER:
            case OFFSET_RANGE:
            case DELAY:
            case SCROLL_MODE:
            case SHOW_IN_LAUNCHER:
                num = db.update("preferences", values, selection, selectionArgs);
                break;
        }
        if (num != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        // db.close();
        return num;
    }

}
