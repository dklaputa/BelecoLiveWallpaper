package com.mylaputa.beleco;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.SurfaceHolder;

import com.mylaputa.beleco.LiveWallpaperRenderer.Callbacks;
import com.mylaputa.beleco.utils.Preferences.Preference;

import net.rbgrn.android.glwallpaperservice.GLWallpaperService;

public class LiveWallpaperService extends GLWallpaperService {

    public static final int SENSOR_RATE = 20;
    private final static String TAG = "LiveWallpaperService";

    @Override
    public Engine onCreateEngine() {
        return new MyEngine();
    }

    class MyEngine extends GLEngine implements SensorEventListener, Callbacks {
        // private SharedPreferences preference;
        private ContentResolver contentResolver;
        private WallpaperPreferenceObserver wallpaperPreferenceObserver;
        private OffsetPreferenceObserver offsetPreferenceObserver;
        private DelayPreferenceObserver delayPreferenceObserver;
        private ScrollPreferenceObserver scrollPreferenceObserver;

        private LiveWallpaperRenderer renderer;
        private SensorManager sensorManager;
        private BroadcastReceiver powerSaverChangeReceiver;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.i(TAG, "Create");
            // Get sensormanager and register as listener.
            setEGLContextClientVersion(2);
            setEGLConfigChooser(8, 8, 8, 0, 0, 0);
            renderer = new LiveWallpaperRenderer(LiveWallpaperService.this, this);
            setRenderer(renderer);
            setRenderMode(RENDERMODE_WHEN_DIRTY);

            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            // preference = LiveWallpaperService.this.getSharedPreferences(
            // Constant.SHARED_PREFS_NAME, MODE_PRIVATE);
            // preference.registerOnSharedPreferenceChangeListener(this);
            // onSharedPreferenceChanged(preference, "all");
            contentResolver = getContentResolver();
            wallpaperPreferenceObserver = new WallpaperPreferenceObserver(
                    new Handler());
            offsetPreferenceObserver = new OffsetPreferenceObserver(
                    new Handler());
            delayPreferenceObserver = new DelayPreferenceObserver(new Handler());
            scrollPreferenceObserver = new ScrollPreferenceObserver(
                    new Handler());
            contentResolver.registerContentObserver(Preference.WALLPAPER_URI,
                    false, wallpaperPreferenceObserver);
            contentResolver.registerContentObserver(
                    Preference.OFFSET_RANGE_URI, false,
                    offsetPreferenceObserver);
            contentResolver.registerContentObserver(Preference.DELAY_URI,
                    false, delayPreferenceObserver);
            contentResolver.registerContentObserver(Preference.SCROLL_MODE_URI,
                    false, scrollPreferenceObserver);
            Cursor cursor = contentResolver.query(Preference.ALL_URI, null,
                    null, null, null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    // mySetOffsetNotificationsEnabled(scrollMode);
                    // renderer.setOffset(0.5f, 0.5f);
                    renderer.setIsDefaultWallpaper(cursor.getInt(0));
                    renderer.setBiasRange(cursor.getInt(1));
                    renderer.setDelay(cursor.getInt(2) + 1);
                    renderer.setOffsetMode(!isPreview() && cursor.getInt(3) == 1);
                }
                cursor.close();
            }
            if (Build.VERSION.SDK_INT >= 21) {
                powerSaverChangeReceiver = new BroadcastReceiver() {
                    @TargetApi(21)
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                        if (pm.isPowerSaveMode()) {
                            renderer.setRefreshRate(20);
                        } else renderer.setRefreshRate(80);
                    }
                };

                IntentFilter filter = new IntentFilter();
                filter.addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED);
                registerReceiver(powerSaverChangeReceiver, filter);
            }
        }

        @Override
        public void onDestroy() {
            // Unregister this as listener
            sensorManager.unregisterListener(this);
            if (Build.VERSION.SDK_INT >= 21) {
                unregisterReceiver(powerSaverChangeReceiver);
            }
            // mHandler.removeCallbacks(drawTarget);
            // preference.unregisterOnSharedPreferenceChangeListener(this);
            contentResolver
                    .unregisterContentObserver(wallpaperPreferenceObserver);
            contentResolver.unregisterContentObserver(offsetPreferenceObserver);
            contentResolver.unregisterContentObserver(delayPreferenceObserver);
            contentResolver.unregisterContentObserver(scrollPreferenceObserver);
            // Kill renderer
            if (renderer != null) {
                renderer.release(); // assuming yours has this method - it
                // should!
            }
            super.onDestroy();

            System.gc();
            Log.i(TAG, "Destroyed");
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                Log.i(TAG, "VisibilityTrue");
                sensorManager
                        .registerListener(this, sensorManager
                                        .getDefaultSensor(Sensor.TYPE_ORIENTATION),
                                1000000 / SENSOR_RATE);
                renderer.startTransition();
            } else {
                Log.i(TAG, "VisibilityFalse");
                sensorManager.unregisterListener(this);
                renderer.stopTransition();
                // mHandler.removeCallbacks(drawTarget);
            }
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                                     float xOffsetStep, float yOffsetStep, int xPixelOffset,
                                     int yPixelOffset) {
            renderer.setOffset(xOffset, yOffset);
            renderer.setOffsetStep(xOffsetStep, yOffsetStep);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            renderer.setOrientationAngle(values[2], values[1]);
        }

        @Override
        public void requestRender() {
            super.requestRender();
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

        class OffsetPreferenceObserver extends ContentObserver {

            OffsetPreferenceObserver(Handler handler) {
                super(handler);
            }

            @Override
            public void onChange(boolean selfChange) {
                Cursor cursor = contentResolver.query(
                        Preference.OFFSET_RANGE_URI, null, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        renderer.setBiasRange(cursor.getInt(0));
                    }
                    cursor.close();
                }
            }
        }

        class DelayPreferenceObserver extends ContentObserver {

            DelayPreferenceObserver(Handler handler) {
                super(handler);
            }

            @Override
            public void onChange(boolean selfChange) {
                Cursor cursor = contentResolver.query(Preference.DELAY_URI,
                        null, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        renderer.setDelay(cursor.getInt(0) + 1);
                    }
                    cursor.close();
                }
            }
        }

        class ScrollPreferenceObserver extends ContentObserver {

            ScrollPreferenceObserver(Handler handler) {
                super(handler);
            }

            @Override
            public void onChange(boolean selfChange) {
                Cursor cursor = contentResolver.query(
                        Preference.SCROLL_MODE_URI, null, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        renderer.setOffsetMode(!isPreview()
                                && cursor.getInt(0) == 1);
                    }
                    cursor.close();
                }
            }
        }
    }

}
