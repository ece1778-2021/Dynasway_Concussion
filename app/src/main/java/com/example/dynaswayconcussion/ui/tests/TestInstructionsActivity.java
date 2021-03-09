package com.example.dynaswayconcussion.ui.tests;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.dynaswayconcussion.R;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TestInstructionsActivity extends AppCompatActivity {

    int testType = -1;
    Random rand = new Random();
    List<Integer> countdownNums = Arrays.asList(3, 7, 11, 13, 19);
    int startingNum = rand.nextInt(700) + 300;
    int countdownNum = countdownNums.get(rand.nextInt(countdownNums.size()));

    TextView txtActivityInstructions;
    TextView txtCognetiveInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_instructions);

        txtActivityInstructions = findViewById(R.id.txtTestActivityInstructions);
        txtCognetiveInstructions = findViewById(R.id.txtTestCognetiveInstructions);

        testType = getIntent().getIntExtra("test_type", -1);

        String activityInstructions = "";
        String cognetiveInstructions = "None";

        if (testType == R.string.static_test_regular)
        {
            activityInstructions = "Place the phone in the waistband of your pants. " +
                    "Try to remain as still as possible with your feet shoulder width apart." +
                    "The test starts 5 seconds after the button is pressed and lasts for 30 seconds. " +
                    "The start and end of the test are announced with beep";
        }

        else if (testType == R.string.static_test_tandem)
        {
            activityInstructions = "Place the phone in the waistband of your pants. " +
                    "Try to remain as still as possible with one toe touching the heel of the opposite foot." +
                    "The test starts 5 seconds after the button is pressed and lasts for 30 seconds. " +
                    "The start and end of the test are announced with beep";
        }

        else if (testType == R.string.static_test_regular_dual_task)
        {
            activityInstructions = "Place the phone in the waistband of your pants. " +
                    "Try to remain as still as possible with one toe touching the heel of the opposite foot." +
                    "The test starts 5 seconds after the button is pressed and lasts for 30 seconds. " +
                    "The start and end of the test are announced with beep";
            cognetiveInstructions = MessageFormat.format("In your head, count numbers starting from {0}, " +
                    "going down by {1}", startingNum, countdownNum);
        }

        else if (testType == R.string.static_test_tandem_dual_task)
        {
            activityInstructions = "Place the phone in the waistband of your pants. " +
                    "Try to remain as still as possible with one toe touching the heel of the opposite foot." +
                    "The test starts 5 seconds after the button is pressed and lasts for 30 seconds. " +
                    "The start and end of the test are announced with beep";
            cognetiveInstructions = MessageFormat.format("In your head, count numbers starting from {0}, " +
                    "going down by {1}", startingNum, countdownNum);
        }

        else if (testType == R.string.dynamic_test_regular)
        {
            activityInstructions = "walk...";
        }

        else if (testType == R.string.dynamic_test_tandem)
        {
            activityInstructions = "walk...";
        }

        else if (testType == R.string.dynamic_test_regular_dual_task)
        {
            activityInstructions = "walk...";
            cognetiveInstructions = MessageFormat.format("In your head, count numbers starting from {0}, " +
                    "going down by {1}", startingNum, countdownNum);
        }

        else if (testType == R.string.dynamic_test_tandem_dual_task)
        {
            activityInstructions = "walk...";
            cognetiveInstructions = MessageFormat.format("In your head, count numbers starting from {0}, " +
                    "going down by {1}", startingNum, countdownNum);
        }

        else
        {
            try {
                throw new Exception("Test type not recognized!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        txtActivityInstructions.setText(activityInstructions);
        txtCognetiveInstructions.setText(cognetiveInstructions);
    }

    public void btnBaselineTest_onClick(View view) {
        Intent intent = new Intent(this, TestActivity.class);
        intent.putExtra("test_type", testType);
        intent.putExtra("is_baseline", true);
        startActivity(intent);
    }

    public void btnPostInjuryTest_onClick(View view) {
        Intent intent = new Intent(this, TestActivity.class);
        intent.putExtra("test_type", testType);
        intent.putExtra("is_baseline", false);
        startActivity(intent);
    }
}