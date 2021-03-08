package com.example.dynaswayconcussion.ui.tests;

import androidx.appcompat.app.AppCompatActivity;

import android.media.ToneGenerator;
import android.os.Bundle;
import android.widget.Toast;

import com.example.dynaswayconcussion.R;

public class TestInstructionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_test);

        String testType = getIntent().getStringExtra("test_type");

        String stanceInstructions = "";
        String cognetiveInstructions = "";

        if (testType.equals(getString(R.string.static_test_regular)))
        {
            stanceInstructions = "When you are ready, press the \"Start test\" button and place the " +
                    "phone in the waistband of your pants. Try to remain as still as possible with your feet shoulder width apart." +
                    "The test starts 5 seconds after the button is pressed and lasts for 30 seconds. " +
                    "The start and end of the test are announced with beep";
        }

        else if (testType.equals(getString(R.string.static_test_tandem)))
        {
            stanceInstructions = "When you are ready, press the \"Start test\" button and place the " +
                    "phone in the waistband of your pants. Try to remain as still as possible with one toe touching the heel of the opposite foot." +
                    "The test starts 5 seconds after the button is pressed and lasts for 30 seconds. " +
                    "The start and end of the test are announced with beep";
        }

        else if (testType.equals(getString(R.string.static_test_regular_dual_task)))
        {
            stanceInstructions = "When you are ready, press the \"Start test\" button and place the " +
                    "phone in the waistband of your pants. Try to remain as still as possible with one toe touching the heel of the opposite foot." +
                    "The test starts 5 seconds after the button is pressed and lasts for 30 seconds. " +
                    "The start and end of the test are announced with beep";
        }

        else if (testType.equals(getString(R.string.static_test_tandem_dual_task)))
        {

        }

        else if (testType.equals(getString(R.string.dynamic_test_regular)))
        {

        }

        else if (testType.equals(getString(R.string.dynamic_test_tandem)))
        {

        }

        else if (testType.equals(getString(R.string.dynamic_test_regular_dual_task)))
        {

        }

        else if (testType.equals(getString(R.string.dynamic_test_tandem_dual_task)))
        {

        }

        else
        {
            try {
                throw new Exception("Test type not recognized!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}