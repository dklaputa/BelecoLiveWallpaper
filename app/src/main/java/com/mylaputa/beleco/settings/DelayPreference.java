package com.mylaputa.beleco.settings;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mylaputa.beleco.R;
import com.mylaputa.beleco.utils.Preferences.Preference;

class DelayPreference extends MyDialogPreference {
    private final Context mContext;
    private int mValue = 2;

    public DelayPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mValue = getPersistedInt(2);
    }

    public DelayPreference(Context context) {
        this(context, null);
    }

    // @Override
    // public void onActivityDestroy() {
    // super.onActivityDestroy();
    // }

    /**
     * Sets the value of the key. This should be one of the entries in
     * {@link #getEntryValues()}.
     *
     * @param value The value to set for the key.
     */
    public void setValue(int value) {
        // Always persist/notify the first time.
        // final boolean changed = (mValue != value);
        // if (changed) {
        // mValue = value;
        persistInt(value);
        notifyChanged();
        // }
    }

    /**
     * Returns the summary of this ListPreference. If the summary has a
     * {@linkplain String#format String formatting} marker in it (i.e.
     * "%s" or "%1$s"), then the current entry value will be substituted in its
     * place.
     *
     * @return the summary with appropriate string substitution
     */
    @Override
    public CharSequence getSummary() {
        return mValue + "x";
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        super.onPrepareDialogBuilder(builder);
        View v = View.inflate(mContext, R.layout.seekbar_preference, null);
        final TextView textView = (TextView) v.findViewById(R.id.textView);
        final SeekBar seekBar = (SeekBar) v.findViewById(R.id.seekBar);
        final TextView messageTextView = (TextView) v
                .findViewById(R.id.message);
        messageTextView.setText(R.string.setting6_description);
        seekBar.setMax(6);
        seekBar.setProgress(mValue);
        textView.setText(mValue + "x");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                textView.setText(progress + "x");
            }
        });
        builder.setView(v);
        builder.setPositiveButton(getPositiveButtonText(),
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        mValue = seekBar.getProgress();
                        DelayPreference.this.onClick(dialog,
                                DialogInterface.BUTTON_POSITIVE);
                        dialog.dismiss();
                    }

                });
    }

    //
    // @Override
    // protected void onSetInitialValue(boolean restoreValue, Object
    // defaultValue) {
    // // mValue = restoreValue ? getPersistedInt(mValue) : (int) defaultValue;
    // Log.d("----------------", "delay");
    // mValue = getPersistedInt(2);
    // }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            // String value = mResult == 0 ? "default" : "not default";
            if (callChangeListener(mValue)) {
                setValue(mValue);
            }
        }
    }

    @Override
    Uri getUri() {
        // TODO Auto-generated method stub
        return Preference.DELAY_URI;
    }

    @Override
    String getContentKey() {
        // TODO Auto-generated method stub
        return Preference.DELAY;
    }

}
