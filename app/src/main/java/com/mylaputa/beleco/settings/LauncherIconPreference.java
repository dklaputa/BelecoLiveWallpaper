package com.mylaputa.beleco.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;

import com.mylaputa.beleco.R;
import com.mylaputa.beleco.utils.Preferences.Preference;

class LauncherIconPreference extends MyCheckBoxPreference {
    private final Context mContext;
    // private boolean isDialogShowing = false;

    private Dialog mDialog;

    // private Dialog mDialog;

    public LauncherIconPreference(final Context context,
                                  final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public LauncherIconPreference(final Context context) {
        this(context, null);
    }

    @Override
    protected void onClick() {
        // super.onClick();

        if (isChecked()) {
            showDialog();

        } else {
            super.onClick();
            PackageManager p = mContext.getPackageManager();
            p.setComponentEnabledSetting(
                    new ComponentName(mContext.getPackageName(), mContext
                            .getPackageName() + ".LauncherActivity"),
                    PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
                    PackageManager.DONT_KILL_APP);
        }
    }

    private void showDialog() {
        mDialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.setting5_dialog_Title)
                .setMessage(R.string.setting5_dialog)
                .setPositiveButton(R.string.setting5_dialog_OK,
                        new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
                                LauncherIconPreference.super.onClick();
                                PackageManager p = mContext.getPackageManager();
                                p.setComponentEnabledSetting(
                                        new ComponentName(mContext
                                                .getPackageName(), mContext
                                                .getPackageName()
                                                + ".LauncherActivity"),
                                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                        PackageManager.DONT_KILL_APP);
                                Intent intent = new Intent(
                                        "com.mylaputa.beleco.LauncherActivity.finish");
                                mContext.sendBroadcast(intent);
                            }
                        }).setNegativeButton(R.string.dialog_Cancel, null)
                .show();
    }

    @Override
    Uri getUri() {
        // TODO Auto-generated method stub
        return Preference.SHOW_IN_LAUNCHER_URI;
    }

    @Override
    String getContentKey() {
        // TODO Auto-generated method stub
        return Preference.SHOW_IN_LAUNCHER;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable instanceState = super.onSaveInstanceState();
        if (mDialog == null || !mDialog.isShowing()) {
            return instanceState;
        }
        final SavedState myState = new SavedState(instanceState);
        myState.isDialogShowing = true;
        // mDialog.dismiss();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        if (myState.isDialogShowing) {
            showDialog();
        }
    }

    public void onActivityDestory() {
        if (mDialog == null || !mDialog.isShowing()) {
            return;
        }
        mDialog.dismiss();
    }

    private static class SavedState extends BaseSavedState {
        boolean isDialogShowing;

        public SavedState(Parcel source) {
            super(source);
            isDialogShowing = source.readInt() == 1;
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(isDialogShowing ? 1 : 0);
        }
    }

}
