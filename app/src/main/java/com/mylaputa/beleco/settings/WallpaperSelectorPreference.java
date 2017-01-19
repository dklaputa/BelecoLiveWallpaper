/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mylaputa.beleco.settings;

import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import com.mylaputa.beleco.R;
import com.mylaputa.beleco.utils.Constant;
import com.mylaputa.beleco.utils.Preferences.Preference;

import java.io.File;

/**
 * A {@link Preference} that displays a list of entries as a dialog.
 * <p>
 * This preference will store a string into the SharedPreferences. This string
 * will be the value from the {@link #setEntryValues(CharSequence[])} array.
 *
 * @attr ref android.R.styleable#ListPreference_entries
 * @attr ref android.R.styleable#ListPreference_entryValues
 */
class WallpaperSelectorPreference extends MyDialogPreference {

    private int mValue = 2;
    private Fragment mFragment;

    public WallpaperSelectorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mValue = getPersistedInt(2);
    }

    public WallpaperSelectorPreference(Context context) {
        this(context, null);
    }

    public void setFragment(Fragment fragment) {
        mFragment = fragment;
    }

    @Override
    public void onActivityDestroy() {
        // Log.d("WallpaperSelectorPreference", "onActivityDestroy");
        mFragment = null;
        super.onActivityDestroy();
    }

    /**
     * Sets the value of the key. This should be one of the entries in
     * {@link #getEntryValues()}.
     *
     * @param value The value to set for the key.
     */
    public void setValue(int value) {
        // Always persist/notify the first time.
        final boolean changed = (mValue != value);
        if (changed) {
            mValue = value;
            persistInt(value);
            notifyChanged();
            Log.i("wallpaperselect", "notifychanged");
        }
        if (mFragment != null)
            Toast.makeText(mFragment.getActivity(), R.string.toast_3,
                    Toast.LENGTH_SHORT).show();
    }

    // /**
    // * Returns the summary of this ListPreference. If the summary has a
    // * {@linkplain java.lang.String#format String formatting} marker in it
    // (i.e.
    // * "%s" or "%1$s"), then the current entry value will be substituted in
    // its
    // * place.
    // *
    // * @return the summary with appropriate string substitution
    // */
    // @Override
    // public CharSequence getSummary() {
    // if (mValue == null) {
    // return super.getSummary();
    // } else {
    // return mValue.equals("default") ? getContext().getString(
    // R.string.setting1_description_1) : mValue;
    // }
    // }

    /**
     * Returns the index of the given value (in the entry values array).
     *
     * @param value The value whose index should be returned.
     * @return The index of the value, or -1 if not found.
     */
    public int isDefault(int value) {
        if (value == 2)
            return 0;
        return 1;
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        super.onPrepareDialogBuilder(builder);

        int mIsDefault = isDefault(mValue);
        builder.setSingleChoiceItems(new CharSequence[]{
                        getContext().getString(R.string.setting1_description_1),
                        getContext().getString(R.string.setting1_description_2)},
                mIsDefault, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            /*
							 * Clicking on an item simulates the positive button
							 * click, and dismisses the dialog.
							 */
                            WallpaperSelectorPreference.this.onClick(dialog,
                                    DialogInterface.BUTTON_POSITIVE);
                            dialog.dismiss();
                        } else {
                            choosePhotos();
                            // Intent intent = new Intent(
                            // Intent.ACTION_PICK,
                            // android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            // mFragment.startActivityForResult(intent, 0);

                            WallpaperSelectorPreference.this.onClick(dialog,
                                    DialogInterface.BUTTON_NEGATIVE);
                            dialog.dismiss();
                        }
                    }
                });

		/*
		 * The typical interaction for list-based dialogs is to have
		 * click-on-an-item dismiss the dialog instead of the user having to
		 * press 'Ok'.
		 */
        builder.setPositiveButton(null, null);
    }

    // @Override
    // protected void onSetInitialValue(boolean restoreValue, Object
    // defaultValue) {
    // // setValue(restoreValue ? getPersistedInt(mValue)
    // // : (Integer) defaultValue);
    // mValue = getPersistedInt(2);
    // Log.d("WallpaperSelectorPreference", "onSetInitialValue");
    // }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            // String value = mResult == 0 ? "default" : "not default";
            if (callChangeListener(2)) {
                setValue(2);
                new File(mFragment.getActivity().getFilesDir() + "/"
                        + Constant.CACHE).delete();
            }
        }
    }

    public void setCustomWallpaperSucceed() {
        int value;
        if (mValue == 2)
            value = 0;
        else
            value = (mValue + 1) % 2;
        if (callChangeListener(value)) {
            setValue(value);
        }
    }

    private void choosePhotos() {
        if (Build.VERSION.SDK_INT >= 19) {
            // Documents API
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            // intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            mFragment.startActivityForResult(intent, 0);

        } else {
            // Older API
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            // intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            mFragment.startActivityForResult(intent, 0);
        }
    }

    @Override
    Uri getUri() {
        // TODO Auto-generated method stub
        return Preference.WALLPAPER_URI;
    }

    @Override
    String getContentKey() {
        // TODO Auto-generated method stub
        return Preference.WALLPAPER;
    }
}
