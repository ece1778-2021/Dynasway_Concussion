package com.example.dynaswayconcussion.Tests;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.dynaswayconcussion.Utils.RollingList;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class MotionSensor implements SensorEventListener {

    private final IMotionSensorListener listener;
    private RollingList<double[]> accelHistory;

    private List<Double> RawTestHistory = new ArrayList<>();
    private List<Double> FilteredTestHistory = new ArrayList<>();

    private boolean isRecording;

    public MotionSensor(Context context, IMotionSensorListener listener)
    {
        this.listener = listener;
        accelHistory = new RollingList<>(10);

        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor linearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        sensorManager.registerListener(this, linearAcceleration, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensor = event.sensor.getType();

        if (sensor == Sensor.TYPE_LINEAR_ACCELERATION)
        {
            double[] acceleration = new double[] {
                    event.values[0],
                    event.values[1],
                    event.values[2]
            };

            accelHistory.add(acceleration);
            generateRaw();
            generateAxisFiltered();

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void generateRaw()
    {
        double[] accelLast = accelHistory.get(accelHistory.getSize() - 1);
        double accelTotalLast = calculateTotalAccel(accelLast);
        listener.onRawMotionAvailable(accelLast, accelTotalLast);

        if (isRecording)
        {
            FilteredTestHistory.add(accelTotalLast);
        }
    }

    private void generateAxisFiltered()
    {
        double xSum = 0;
        double ySum = 0;
        double zSum = 0;

        double historySize = accelHistory.getSize();

        for (int i = 0; i < historySize; i++)
        {
            xSum = xSum + accelHistory.get(i)[0];
            ySum = ySum + accelHistory.get(i)[1];
            zSum = zSum + accelHistory.get(i)[2];
        }

        xSum = xSum / historySize;
        ySum = ySum / historySize;
        zSum = zSum / historySize;

        double[] accelFiltered = new double[] {xSum, ySum, zSum};
        double accelTotalFiltered = calculateTotalAccel(accelFiltered);

        if (isRecording)
        {
            FilteredTestHistory.add(accelTotalFiltered);
        }

        listener.onAxisFilteredMotionAvailable(accelFiltered, accelTotalFiltered);

    }

    private void generateTestResult()
    {
        double rawResult = calculateTestResult(RawTestHistory);
        double filteredResult = calculateTestResult(FilteredTestHistory);

        listener.onRawTestResultAvailable(rawResult);
        listener.onFilteredTestResultAvailable(filteredResult);
    }

    private double calculateTestResult(List<Double> testHistory)
    {
        double squareSum = 0;
        int n = testHistory.size();
        for (int i=0; i<n; i++)
        {
            squareSum += pow(testHistory.get(i), 2);
        }

        double rms = sqrt(squareSum / (double)n);
        return rms;
    }

    private double calculateTotalAccel(double[] accel)
    {
        double totalAcceleration = sqrt(pow(accel[0], 2) + pow(accel[1], 2) + pow(accel[2], 2));
        return totalAcceleration;
    }
}
