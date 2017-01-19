package com.mylaputa.beleco.settings;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;

import com.mylaputa.beleco.utils.Preferences.Preference;

public class ScrollModePreference extends MyCheckBoxPreference {
    public ScrollModePreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollModePreference(final Context context) {
        this(context, null);
    }

    @Override
    Uri getUri() {
        // TODO Auto-generated method stub
        return Preference.SCROLL_MODE_URI;
    }

    @Override
    String getContentKey() {
        // TODO Auto-generated method stub
        return Preference.SCROLL_MODE;
    }

}
