package com.example.dynaswayconcussion.Tests.DynamicTest;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

public interface IStepCounterListener {
    void onSensorChanged(SensorEvent event);
    void onAccuracyChanged(Sensor sensor, int accuracy);
}
