package com.mylaputa.beleco.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by dklap on 1/25/2017.
 */

public class RotationSensor implements SensorEventListener {
    private int sampleRate;
    private SensorManager sensorManager;
    private Callback callback;
    private float[] initialRotation;
    private boolean listenerRegistered = false;

    public RotationSensor(Callback callback, int sampleRate) {
        this.sampleRate = sampleRate;
        this.callback = callback;
        sensorManager = (SensorManager) callback.getContext().getSystemService(Context.SENSOR_SERVICE);
    }

    public void register() {
        if (listenerRegistered) return;
        sensorManager
                .registerListener(this, sensorManager
                                .getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                        1000000 / sampleRate);
        listenerRegistered = true;
    }

    public void unregister() {
        if (!listenerRegistered) return;
        sensorManager.unregisterListener(this);
        listenerRegistered = false;
        initialRotation = null;
    }

    public void destroy() {
        callback = null;
        sensorManager = null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] r = new float[9];
        SensorManager.getRotationMatrixFromVector(r, event.values);
        if (initialRotation == null) {
            initialRotation = r;
            return;
        }
        float[] change = new float[3];
        SensorManager.getAngleChange(change, r, initialRotation);
        callback.setOrientationAngle(change);
//        Log.i("Sensor", values[0] + ", " + values[1] + ", " + values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public interface Callback {
        void setOrientationAngle(float[] values);

        Context getContext();
    }
}
