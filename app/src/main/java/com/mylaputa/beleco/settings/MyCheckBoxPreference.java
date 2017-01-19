package com.mylaputa.beleco.settings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;

public abstract class MyCheckBoxPreference extends CheckBoxPreference {
    Context mContext;

    public MyCheckBoxPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public MyCheckBoxPreference(final Context context) {
        this(context, null);
    }

    @Override
    protected boolean persistBoolean(boolean value) {
        ContentValues contentValues = new ContentValues();
        if (value) {
            contentValues.put(getContentKey(), 1);
        } else {
            contentValues.put(getContentKey(), 0);
        }
        if (mContext.getContentResolver().update(getUri(), contentValues, null,
                null) != 0) {
            return true;
        }
        return false;
    }

    @Override
    protected boolean getPersistedBoolean(boolean defaultReturnValue) {
        Cursor cursor = mContext.getContentResolver().query(getUri(), null,
                null, null, null);
        if (cursor.moveToNext()) {
            return cursor.getInt(0) == 1;
        }
        cursor.close();
        return defaultReturnValue;
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setChecked(getPersistedBoolean((boolean) defaultValue));
    }

    // @Override
    // protected void onAttachedToHierarchy(PreferenceManager preferenceManager)
    // {
    // onSetInitialValue(true, null);
    // }

    abstract Uri getUri();

    abstract String getContentKey();
}
