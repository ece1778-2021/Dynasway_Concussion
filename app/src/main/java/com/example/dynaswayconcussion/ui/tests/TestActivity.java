package com.example.dynaswayconcussion.ui.tests;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dynaswayconcussion.R;
import com.example.dynaswayconcussion.Tests.DynamicTest.DynamicTest;
import com.example.dynaswayconcussion.Tests.ITest;
import com.example.dynaswayconcussion.Tests.StaticTest.StaticTest;

import java.text.DecimalFormat;
import java.text.MessageFormat;

public class TestActivity extends AppCompatActivity {

    int TEST_STATE_INITIALIZING = 0;
    int TEST_STATE_COUNTDOWN = 1;
    int TEST_STATE_RUNNING = 2;
    int TEST_STATE_FINISHING = 3;
    int TEST_STATE_FINISHED = 4;

    int testState = 0;

    TextView txtTestTimer;
    TextView txtTestState;
    TextView txtTestCompleted;
    Button btnCancelTest;
    Button btnReturn;

    long startTime = 0;
    int delayDuration = 2000;
    int testDuration = 5000;
    boolean countdown;

    ITest test;
    boolean isBaseline = false;

    DecimalFormat decimalFormat = new DecimalFormat("00");

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long timeEllapsed = System.currentTimeMillis() - startTime;
            long timeRemaining = 0;
            if (testState == TEST_STATE_INITIALIZING)
            {
                btnCancelTest.setVisibility(View.VISIBLE);
                btnReturn.setVisibility(View.INVISIBLE);
                txtTestCompleted.setVisibility(View.INVISIBLE);
                testState= TEST_STATE_COUNTDOWN;
                }

            else if (testState == TEST_STATE_COUNTDOWN)
            {
                txtTestState.setText("Test starting in...");
                txtTestTimer.setTextColor(getColor(R.color.main_pink));
                timeRemaining = delayDuration - timeEllapsed;

                if (timeRemaining <= 0)
                {
                    ToneGenerator toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    toneGen.startTone(ToneGenerator.TONE_PROP_BEEP2,300);
                    testState = TEST_STATE_RUNNING;
                }
            }

            else if (testState == TEST_STATE_RUNNING)
            {
                txtTestState.setText("Test ending in...");
                txtTestTimer.setTextColor(getColor(R.color.main_gray));
                timeRemaining = testDuration - (timeEllapsed - delayDuration);
                if (timeRemaining <= 0)
                {
                    ToneGenerator toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    toneGen.startTone(ToneGenerator.TONE_PROP_BEEP2,300);
                    testState = TEST_STATE_FINISHING;
                }
            }

            else if (testState == TEST_STATE_FINISHING)
            {
                btnCancelTest.setVisibility(View.INVISIBLE);
                btnReturn.setVisibility(View.VISIBLE);
                txtTestCompleted.setVisibility(View.VISIBLE);

                timeRemaining = 0;
                onTestFinished();
                testState = TEST_STATE_FINISHED;
            }
            
            int seconds = (int) (timeRemaining / 1000);
            seconds = seconds % 60;
            long millis = timeRemaining % 1000;

            String timeString = MessageFormat.format("{0}s {1}",
                    decimalFormat.format(seconds),
                    decimalFormat.format(millis));

            txtTestTimer.setText(timeString);
            timerHandler.postDelayed(this, 10);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        txtTestTimer = findViewById(R.id.txtTestTimer);
        txtTestState = findViewById(R.id.txtTestState);
        txtTestCompleted = findViewById(R.id.txtTestCompleted);
        btnCancelTest = findViewById(R.id.btnCancelTest);
        btnReturn = findViewById(R.id.btnReturn);

        Intent intent = getIntent();
        int testType = intent.getIntExtra("test_type", 0);
        isBaseline = intent.getBooleanExtra("is_baseline", false);

        switch (testType)
        {
            case R.string.static_test_regular:
            case R.string.static_test_tandem:
            case R.string.static_test_regular_dual_task:
            case R.string.static_test_tandem_dual_task:
                test = new StaticTest(this);
                break;
            case R.string.dynamic_test_regular:
            case R.string.dynamic_test_tandem:
            case R.string.dynamic_test_regular_dual_task:
            case R.string.dynamic_test_tandem_dual_task:
                test = new DynamicTest(this);
                break;
        }

        testState = TEST_STATE_INITIALIZING;
        test.startTest();

        startTime = System.currentTimeMillis();
        countdown = true;
        timerHandler.postDelayed(timerRunnable, 0);

    }

    public void onTestFinished()
    {
        test.stopTest();
        double result = test.getResult();
        Toast.makeText(this, String.valueOf(result), Toast.LENGTH_LONG).show();
        timerHandler.removeCallbacks(timerRunnable);
    }

    public void btnCancelTest_onClick(View view) {
        timerHandler.removeCallbacks(timerRunnable);
        Toast.makeText(this, "Test has been cancelled", Toast.LENGTH_SHORT).show();

        Intent data = new Intent();
        setResult(RESULT_CANCELED, data);
        finish();
    }

    public void btnReturn_onClick(View view) {
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
    }
}