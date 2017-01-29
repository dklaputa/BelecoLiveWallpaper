package com.mylaputa.beleco;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceHolder;

import com.mylaputa.beleco.sensor.RotationSensor;
import com.mylaputa.beleco.utils.Preferences.Preference;

import net.rbgrn.android.glwallpaperservice.GLWallpaperService;

public class LiveWallpaperService extends GLWallpaperService {

    public static final int SENSOR_RATE = 60;
    private final static String TAG = "LiveWallpaperService";

    @Override
    public Engine onCreateEngine() {
        return new MyEngine();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class MyEngine extends GLEngine implements LiveWallpaperRenderer.Callbacks, RotationSensor.Callback, SharedPreferences.OnSharedPreferenceChangeListener {
        // private SharedPreferences preference;
        private ContentResolver contentResolver;
        private WallpaperPreferenceObserver wallpaperPreferenceObserver;

        private LiveWallpaperRenderer renderer;
        private RotationSensor rotationSensor;
        private BroadcastReceiver powerSaverChangeReceiver;

        //        private int sensorFrequency = 40;
        private boolean pauseInSavePowerMode = false;
        private boolean savePowerMode = false;
//        private long time;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.i(TAG, "Create");
            // Get sensormanager and register as listener.
            setEGLContextClientVersion(2);
            setEGLConfigChooser(8, 8, 8, 0, 0, 0);
            renderer = new LiveWallpaperRenderer(LiveWallpaperService.this.getApplicationContext(), this);
            setRenderer(renderer);
            setRenderMode(RENDERMODE_WHEN_DIRTY);

            rotationSensor = new RotationSensor(this, SENSOR_RATE);
//            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(LiveWallpaperService.this);
            preference.registerOnSharedPreferenceChangeListener(this);
            renderer.setBiasRange(preference.getInt("range", 10));
            renderer.setDelay(21 - preference.getInt("deny", 10));
            renderer.setScrollMode(preference.getBoolean("scroll", true));


            setPowerSaverEnabled(preference.getBoolean("power_saver", true));
            // onSharedPreferenceChanged(preference, "all");
            contentResolver = getContentResolver();
            wallpaperPreferenceObserver = new WallpaperPreferenceObserver(
                    new Handler());

            contentResolver.registerContentObserver(Preference.WALLPAPER_URI,
                    false, wallpaperPreferenceObserver);

            Cursor cursor = contentResolver.query(Preference.WALLPAPER_URI, null,
                    null, null, null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    // mySetOffsetNotificationsEnabled(scrollMode);
                    // renderer.setOffset(0.5f, 0.5f);
                    renderer.setIsDefaultWallpaper(cursor.getInt(0));
                }
                cursor.close();
            }
        }

        @Override
        public void onDestroy() {
            // Unregister this as listener
            rotationSensor.unregister();
            rotationSensor.destroy();
            if (Build.VERSION.SDK_INT >= 21) {
                unregisterReceiver(powerSaverChangeReceiver);
            }
            // mHandler.removeCallbacks(drawTarget);
            // preference.unregisterOnSharedPreferenceChangeListener(this);
            contentResolver
                    .unregisterContentObserver(wallpaperPreferenceObserver);
            // Kill renderer
            if (renderer != null) {
                renderer.release(); // assuming yours has this method - it
                // should!
            }
            super.onDestroy();

//            System.gc();
            Log.i(TAG, "Destroyed");
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (!pauseInSavePowerMode || !savePowerMode) {
                if (visible) {
                    Log.i(TAG, "VisibilityTrue");
                    rotationSensor.register();
                    renderer.startTransition();
                } else {
                    Log.i(TAG, "VisibilityFalse");
                    rotationSensor.unregister();
                    renderer.stopTransition();
//                    renderer.clearOrientationOffsetQueue();
                    // mHandler.removeCallbacks(drawTarget);
                }
            } else {
                if (visible) {
                    Log.i(TAG, "VisibilityTrue");
                    renderer.startTransition();
                } else {
                    Log.i(TAG, "VisibilityFalse");
                    renderer.stopTransition();
//                    renderer.clearOrientationOffsetQueue();
                    // mHandler.removeCallbacks(drawTarget);
                }
            }
        }

//        void changeSensorFrequency(int frequency) {
//            if (isVisible()) {
//                unregisterSensorListener();
//                registerSensorListener(frequency);
//            }
//            sensorFrequency = frequency;
//        }

//        void registerSensorListener() {
//            Log.i(TAG, "Sensor registered");
//            sensorManager
//                    .registerListener(this, sensorManager
//                                    .getDefaultSensor(Sensor.TYPE_ORIENTATION),
//                            1000000 / SENSOR_RATE);
//        }
//
//        void unregisterSensorListener() {
//            Log.i(TAG, "Sensor unregistered");
//            sensorManager.unregisterListener(this);
//        }

        void setPowerSaverEnabled(boolean enabled) {
            if (pauseInSavePowerMode == enabled) return;
            pauseInSavePowerMode = enabled;
            if (Build.VERSION.SDK_INT >= 21) {
                final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                if (pauseInSavePowerMode) {
                    powerSaverChangeReceiver = new BroadcastReceiver() {
                        @TargetApi(21)
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            savePowerMode = pm.isPowerSaveMode();
                            if (savePowerMode && isVisible()) {
                                rotationSensor.unregister();
                                renderer.setOrientationAngle(0, 0);
//                            changeSensorFrequency(10);
//                            renderer.setRefreshRate(15);
                            } else if (!savePowerMode && isVisible()) {
                                rotationSensor.register();
//                            changeSensorFrequency(40);
//                            renderer.setRefreshRate(60);
                            }
                        }
                    };

                    IntentFilter filter = new IntentFilter();
                    filter.addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED);
                    registerReceiver(powerSaverChangeReceiver, filter);
                    savePowerMode = pm.isPowerSaveMode();
                    if (savePowerMode && isVisible()) {
                        rotationSensor.unregister();
                        renderer.setOrientationAngle(0, 0);
                    }
                } else {
                    unregisterReceiver(powerSaverChangeReceiver);
                    savePowerMode = pm.isPowerSaveMode();
                    if (savePowerMode && isVisible()) {
                        rotationSensor.register();
                    }

                }
            }
        }


        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                                     float xOffsetStep, float yOffsetStep, int xPixelOffset,
                                     int yPixelOffset) {
//            Log.i(TAG, xOffset + "," + yOffset + "," + xOffsetStep + "," + yOffsetStep);
            if (!isPreview()) {
                renderer.setOffset(xOffset, yOffset);
                renderer.setOffsetStep(xOffsetStep, yOffsetStep);
            }
        }

        @Override
        public void requestRender() {
            super.requestRender();
        }

        @Override
        public void setOrientationAngle(float[] values) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                renderer.setOrientationAngle(values[1], values[2]);
            else renderer.setOrientationAngle(-values[2], values[1]);
        }

        @Override
        public Context getContext() {
            return LiveWallpaperService.this.getApplicationContext();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case "range":
                    renderer.setBiasRange(sharedPreferences.getInt(key, 10));
                    break;
                case "delay":
                    renderer.setDelay(21 - sharedPreferences.getInt(key, 10));
                    break;
                case "scroll":
                    renderer.setScrollMode(sharedPreferences.getBoolean(key, true));
                    break;
                case "power_saver":
                    setPowerSaverEnabled(sharedPreferences.getBoolean(key, true));
                    break;
            }
        }


        class WallpaperPreferenceObserver extends ContentObserver {

            WallpaperPreferenceObserver(Handler handler) {
                super(handler);
            }

            @Override
            public void onChange(boolean selfChange) {
                Cursor cursor = contentResolver.query(Preference.WALLPAPER_URI,
                        null, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        renderer.setIsDefaultWallpaper(cursor.getInt(0));
                    }
                    cursor.close();
                }
            }
        }

    }

}
