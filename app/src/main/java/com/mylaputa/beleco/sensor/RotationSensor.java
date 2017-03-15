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
    private Callback callback;
    private SensorManager sensorManager;
    private float[] initialRotation;
    private boolean listenerRegistered = false;

    public RotationSensor(Context context, Callback callback, int sampleRate) {
        this.sampleRate = sampleRate;
        this.callback = callback;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void register() {
        if (listenerRegistered) return;
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor
                .TYPE_ROTATION_VECTOR), 1000000 / sampleRate);
        listenerRegistered = true;
    }

    public void unregister() {
        if (!listenerRegistered) return;
        sensorManager.unregisterListener(this);
        listenerRegistered = false;
        initialRotation = null;
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
        callback.onSensorChanged(change);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public interface Callback {
        void onSensorChanged(float[] angle);
    }
}
