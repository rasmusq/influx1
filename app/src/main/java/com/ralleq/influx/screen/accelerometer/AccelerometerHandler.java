package com.ralleq.influx.screen.accelerometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class AccelerometerHandler implements SensorEventListener {

    public static final int
            PORTRAIT_TOP_UP     = 0,
            LANDSCAPE_TOP_LEFT  = 1,
            PORTRAIT_TOP_DOWN   = 2,
            LANDSCAPE_TOP_RIGHT = 3;
    private static final float rotationThreshold = 5;

    private SensorManager sensorManager;
    private Sensor sensor;

    private int orientation = PORTRAIT_TOP_UP;

    public AccelerometerHandler(Context context) {

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager != null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.i(getClass().getName(), "Could not initialize the sensorManager");
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(getClass().getName(), "Gyroscope accuracy changed to: " + accuracy);
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;
        int previousOrientation = orientation;
        if(values[2] < rotationThreshold) {
            if(values[0] < rotationThreshold && values[1] > rotationThreshold)
                orientation = PORTRAIT_TOP_UP;
            if(values[0] > rotationThreshold && values[1] < rotationThreshold)
                orientation = LANDSCAPE_TOP_LEFT;
            if(values[0] > -rotationThreshold && values[1] < -rotationThreshold)
                orientation = PORTRAIT_TOP_DOWN;
            if(values[0] < -rotationThreshold && values[1] > -rotationThreshold)
                orientation = LANDSCAPE_TOP_RIGHT;
        }
        boolean newOrientation = previousOrientation != orientation;
        if(newOrientation) {
            //fetchOrientation(orientation);
        }
    }
    public void forceOrientationUpdate() {
        //fetchOrientation(orientation);
    }

    public int getOrientation() {
        return orientation;
    }
}
