package com.example.dynaswayconcussion.Tests.DynamicTest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.util.Log;

import com.example.dynaswayconcussion.Tests.ITest;

public class DynamicTest implements ITest, IStepCounterListener, SensorEventListener {

    //Constants
    private static final String INFO_TAG = "[Dynamic testing info]";
    private static final long TEST_MIN_MILLIS = 20000; //the test is 20 seconds long
    private static final long SENSOR_MIN_ACTIVATION_TIME = 10000; //the step sensor takes 10 seconds to activate to evade false positives
    public static final int STEP_COUNTER_PERMISSION_CODE = 20;
    private static final int TEST_FAILED = -1;

    //Activity sensor related variables
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean isSensorPresent = false;

    //Initial values during test related variables
    private int currentSteps = 0;
    private boolean isDynamicTestReady = false;
    private int stepsWhenTestStarted = 0;
    private boolean testStarted = false;
    private boolean sensorIsCounting = false;

    //Test results related variables
    private int stepsTakenDuringTest = 0;
    private double stepsPerSecondResult = 0.0;

    //System related variables
    Context ctx;
    DoDynamicTestTask current_task = null;

    //This class requires the following permissions for _ctx:
    //Manifest.permission.ACTIVITY_RECOGNITION
    //The sensor will fail if the given context doesn't have this permission
    public DynamicTest(Context _ctx) {
        this.ctx = _ctx;
        mSensorManager = (SensorManager)this.ctx.getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null)
        {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isSensorPresent = true;
        }
        else
        {
            isSensorPresent = false;
        }
        if(isSensorPresent)
        {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    public void startTest() {
        if (isSensorPresent) {
            stepsWhenTestStarted = currentSteps;
            stepsTakenDuringTest = 0;
            //startDynamicTestButton.setEnabled(false);
            testStarted = true;
            sensorIsCounting = false;
            current_task = new DoDynamicTestTask();
            current_task.execute();
        }
    }

    @Override
    public void stopTest() {
        if (current_task != null) {
            if (current_task.getStatus() == AsyncTask.Status.RUNNING
                    || current_task.getStatus() == AsyncTask.Status.PENDING) {
                testStarted = false;
                sensorIsCounting = false;
                current_task.cancel(true);
            }
        }
    }

    @Override
    public double getResult() {
        return stepsPerSecondResult;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        currentSteps = (int)event.values[0];
        if (!isDynamicTestReady) {
            isDynamicTestReady = true;
            //startDynamicTestButton.setEnabled(true);
        }
        if (testStarted && !sensorIsCounting) {
            sensorIsCounting = true;
        }
        Log.i(INFO_TAG, "Sensor changed, value: " + String.valueOf(currentSteps));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class DoDynamicTestTask extends AsyncTask<Void, Void, Void> {


        long startTime = 0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startTime = System.currentTimeMillis();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
            timedStepsFunction(this.startTime);
            return null;
        }
    }

    private void timedStepsFunction(long startTime) {
        while (testStarted && !sensorIsCounting) {
            stepsWhenTestStarted = currentSteps;
            stepsTakenDuringTest = 0;
            stepsPerSecondResult = 0.0;
            //TODO: beep when this loop is done to tell the use the test started
        }
        long testStartTime = System.currentTimeMillis();
        long currentTime = System.currentTimeMillis();
        while (testStarted) {
            stepsTakenDuringTest = currentSteps - stepsWhenTestStarted;
            currentTime = System.currentTimeMillis();
            double secondsElapsed = ((currentTime - testStartTime) / 1000.0);
            if (secondsElapsed != 0.0) {
                stepsPerSecondResult = (currentSteps - stepsWhenTestStarted) / secondsElapsed;
            }
        }

        /*stepsTakenDuringTest = currentSteps - stepsWhenTestStarted;
        currentTime = System.currentTimeMillis();
        double secondsElapsed = ((currentTime - testStartTime) / 1000.0);
        if (secondsElapsed != 0.0) {
            stepsPerSecondResult = (currentSteps - stepsWhenTestStarted) / secondsElapsed;
        }*/

        testStarted = false;
        sensorIsCounting = false;
    }
}
