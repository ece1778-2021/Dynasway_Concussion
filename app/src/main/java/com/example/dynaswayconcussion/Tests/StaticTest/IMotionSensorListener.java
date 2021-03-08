package com.example.dynaswayconcussion.Tests.StaticTest;

public interface IMotionSensorListener {
    void onRawMotionAvailable(double[] acceleration, double total);
    void onAxisFilteredMotionAvailable(double[] acceleration, double total);
}
