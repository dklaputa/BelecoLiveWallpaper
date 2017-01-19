package com.mylaputa.beleco.utils;

import android.net.Uri;

public class Preferences {
    public static String AUTHORITY = "com.mylaputa.beleco.preferences";

    public static class Preference {
        public static final String WALLPAPER = "wallpaper";
        public static final String OFFSET_RANGE = "offset_range";
        public static final String DELAY = "delay";
        public static final String SCROLL_MODE = "scroll_mode";
        public static final String SHOW_IN_LAUNCHER = "show_in_launcher";
        public static final String ALL = "all";
        public static final Uri WALLPAPER_URI = Uri.parse("content://"
                + AUTHORITY + "/" + WALLPAPER);
        public static final Uri OFFSET_RANGE_URI = Uri.parse("content://"
                + AUTHORITY + "/" + OFFSET_RANGE);
        public static final Uri DELAY_URI = Uri.parse("content://" + AUTHORITY
                + "/" + DELAY);
        public static final Uri SCROLL_MODE_URI = Uri.parse("content://"
                + AUTHORITY + "/" + SCROLL_MODE);
        public static final Uri SHOW_IN_LAUNCHER_URI = Uri.parse("content://"
                + AUTHORITY + "/" + SHOW_IN_LAUNCHER);
        public static final Uri ALL_URI = Uri.parse("content://" + AUTHORITY
                + "/" + ALL);
    }
}
