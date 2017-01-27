package com.mylaputa.beleco.settings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public abstract class MyDialogPreference extends DialogPreference {
    Context mContext;

    public MyDialogPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public MyDialogPreference(final Context context) {
        this(context, null);
    }

    @Override
    protected boolean persistInt(int value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(getContentKey(), value);
        return mContext.getContentResolver().update(getUri(), contentValues, null,
                null) != 0;
    }

    @Override
    protected int getPersistedInt(int defaultReturnValue) {
        Cursor cursor = mContext.getContentResolver().query(getUri(), null,
                null, null, null);
        if (cursor.moveToNext()) {
            return cursor.getInt(0);
        }
        cursor.close();
        return defaultReturnValue;
    }

    // protected void onAttachedToHierarchy(PreferenceManager preferenceManager)
    // {
    // onSetInitialValue(true, null);
    // }
    // @Override
    // protected void onPrepareDialogBuilder(Builder builder) {
    // super.onPrepareDialogBuilder(builder);
    // builder = new AlertDialog.Builder(mContext, R.style.DialogTheme)
    // .setTitle("xx").setPositiveButton(R.string.dialog_OK, this)
    // .setNegativeButton(R.string.dialog_Cancel, this);
    // }
    abstract Uri getUri();

    abstract String getContentKey();
}
