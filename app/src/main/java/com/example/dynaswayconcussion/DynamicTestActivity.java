package com.dynasway.dynamicbalancetestingproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    //Constants
    private static final String INFO_TAG = "[Dynamic testing info]";
    private static final long TEST_MIN_MILLIS = 20000; //the test is 20 seconds long
    private static final long SENSOR_MIN_ACTIVATION_TIME = 10000; //the step sensor takes 10 seconds to activate to evade false positives
    private static final int STEP_COUNTER_PERMISSION_CODE = 20;

    //Activity sensor related variables
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean isSensorPresent = false;

    //Page component related variables
    private TextView steps_during_test_display;
    private Button startDynamicTestButton;

    //Initial values during test related variables
    private int currentSteps = 0;
    private boolean isDynamicTestReady = false;
    private int stepsWhenTestStarted = 0;
    private boolean testStarted = false;
    private boolean sensorIsCounting = false;

    //Test results related variables
    private int stepsTakenDuringTest = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        steps_during_test_display =
                (TextView)findViewById(R.id.steps_during_test_display);
        mSensorManager = (SensorManager)
                this.getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
                != null)
        {
            mSensor =
                    mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isSensorPresent = true;
        }
        else
        {
            isSensorPresent = false;
        }

        startDynamicTestButton = (Button)findViewById(R.id.start_dynamic_test_button);
        startDynamicTestButton.setEnabled(false);
        startDynamicTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSensorPresent) {
                    stepsWhenTestStarted = currentSteps;
                    stepsTakenDuringTest = 0;
                    startDynamicTestButton.setEnabled(false);
                    testStarted = true;
                    sensorIsCounting = false;
                    new DoDynamicTestTask().execute();
                }
            }
        });
        checkIfStepCounterPermissionsEnabled();
    }

    private void checkIfStepCounterPermissionsEnabled() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, STEP_COUNTER_PERMISSION_CODE);
        }
        /*else {
            startDynamicTestButton.setEnabled(true);
        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean isStepCountingGranted = false;
        switch (requestCode) {
            case STEP_COUNTER_PERMISSION_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            isStepCountingGranted = true;
                        }
                    }
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    /*if (isStepCountingGranted) {
                        startDynamicTestButton.setEnabled(true);
                    }
                    else {
                        checkIfStepCounterPermissionsEnabled();
                    }*/
                    if (!isStepCountingGranted) {
                        checkIfStepCounterPermissionsEnabled();
                    }
                } else {
                    Toast.makeText(this, "If the permissions aren't enabled, the test can't be done", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isSensorPresent)
        {
            mSensorManager.registerListener(this, mSensor,
                    SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isSensorPresent)
        {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        currentSteps = (int)event.values[0];
        if (!isDynamicTestReady) {
            isDynamicTestReady = true;
            startDynamicTestButton.setEnabled(true);
        }
        if (testStarted && !sensorIsCounting) {
            sensorIsCounting = true;
        }
        Log.i(INFO_TAG, "Sensor changed, value: " + String.valueOf(currentSteps));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //Async class to apply the loading circle effect over the Firebase loading sequence
    private class DoDynamicTestTask extends AsyncTask<Void, Void, Void> {

        long startTime = 0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //inAnimation = new AlphaAnimation(0f, 1f);
            startTime = System.currentTimeMillis();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            startDynamicTestButton.setEnabled(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            timedStepsFunction(this.startTime);
            return null;
        }
    }

    private void timedStepsFunction(long startTime) {
        //long currentTime = System.currentTimeMillis();
        //long timeElapsed = currentTime - startTime;
        /*while (timeElapsed < SENSOR_MIN_ACTIVATION_TIME) {
            timeElapsed = System.currentTimeMillis() - startTime;
            stepsWhenTestStarted = currentSteps;
        }*/
        while (testStarted && !sensorIsCounting) {
            stepsWhenTestStarted = currentSteps;
            //TODO: beep when this loop is done to tell the use the test started
        }
        startTime = System.currentTimeMillis();
        long timeElapsed = System.currentTimeMillis() - startTime;
        stepsWhenTestStarted = currentSteps;
        while (timeElapsed < TEST_MIN_MILLIS) {
            timeElapsed = System.currentTimeMillis() - startTime;
            stepsTakenDuringTest = currentSteps - stepsWhenTestStarted;
        }
        float secondsElapsed = (timeElapsed / 1000.0f);
        stepsTakenDuringTest = currentSteps - stepsWhenTestStarted;
        String output = "Pace: 0 steps/s";
        if (secondsElapsed > 0.0f) {
            output = "Pace: " + String.valueOf(stepsTakenDuringTest / secondsElapsed) + " steps/s";
        }
        steps_during_test_display.setText(output);

        testStarted = false;
        sensorIsCounting = false;
    }
}