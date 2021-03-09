package com.example.dynaswayconcussion.Tests.StaticTest;

import android.content.Context;

import com.example.dynaswayconcussion.Tests.ITest;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class StaticTest implements ITest, IMotionSensorListener {

    private boolean _isRunning;
    private List<Double> _testHistory = new ArrayList<>();
    private double finalResult;

    public StaticTest(Context context) {
        MotionSensor motionSensor = new MotionSensor(context, this);
    }

    public void startTest()
    {
        _testHistory.clear();
        _isRunning = true;
    }

    public void stopTest()
    {
        _isRunning = false;

    }

    @Override
    public double getResult() {
        finalResult = calculateFinalResult();
        return finalResult;
    }

    public double calculateFinalResult()
    {
        double squareSum = 0;
        int n = _testHistory.size();
        for (int i=0; i<n; i++)
        {
            squareSum += pow(_testHistory.get(i), 2);
        }

        double rms = sqrt(squareSum / (double)n);
        return rms;
    }

    @Override
    public void onRawMotionAvailable(double[] acceleration, double total) {

    }

    @Override
    public void onAxisFilteredMotionAvailable(double[] acceleration, double total) {
        if (_isRunning)
        {
            _testHistory.add(total);
        }
    }

    public double getFinalResult() {
        return finalResult;
    }
}
