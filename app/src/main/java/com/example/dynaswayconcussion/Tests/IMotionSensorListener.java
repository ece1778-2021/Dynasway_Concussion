package com.example.dynaswayconcussion.Tests;

public interface IMotionSensorListener {
    void onRawMotionAvailable(double[] acceleration, double total);
    void onAxisFilteredMotionAvailable(double[] acceleration, double total);
    void onRawTestResultAvailable(double result);
    void onFilteredTestResultAvailable(double result);
}
