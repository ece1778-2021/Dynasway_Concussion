package com.example.dynaswayconcussion.ui.tests;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.dynaswayconcussion.R;
import com.example.dynaswayconcussion.Tests.DynamicTest.camera.CameraActivity;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TestInstructionsActivity extends AppCompatActivity {

    private final int RUN_STATIC_TEST = 0;
    private final int RUN_DYNAMIC_TEST = 1;

    int testType = -1;
    Random rand = new Random();
    List<Integer> countdownNums = Arrays.asList(7, 9, 13);
    int startingNum = rand.nextInt(700) + 300;
    int countdownNum = countdownNums.get(rand.nextInt(countdownNums.size()));

    TextView txtActivityInstructions;
    TextView txtCognitiveInstructions;

    boolean isDynamicTest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_instructions);

        txtActivityInstructions = findViewById(R.id.txtTestActivityInstructions);
        txtCognitiveInstructions = findViewById(R.id.txtTestCognitiveInstructions);

        testType = getIntent().getIntExtra("test_type", -1);
        String title = getString(testType);
        TextView txtTitle = findViewById(R.id.txtTestTitle);
        txtTitle.setText(title);

        String activityInstructions = "";
        String cognitiveInstructions = "None";

        if (testType == R.string.static_test_regular)
        {
            activityInstructions = "Place the phone in the waistband of your pants. " +
                    "Try to remain as still as possible with your feet shoulder width apart. " +
                    "The test starts 5 seconds after the button is pressed and lasts for 30 seconds. " +
                    "The start and end of the test are announced with a beep.";
        }

        else if (testType == R.string.static_test_tandem)
        {
            activityInstructions = "Place the phone in the waistband of your pants. " +
                    "Try to remain as still as possible with one toe touching the heel of the opposite foot. " +
                    "The test starts 5 seconds after the button is pressed and lasts for 30 seconds. " +
                    "The start and end of the test are announced with a beep.";
        }

        else if (testType == R.string.static_test_regular_dual_task)
        {
            activityInstructions = "Place the phone in the waistband of your pants. " +
                    "Try to remain as still as possible with one toe touching the heel of the opposite foot. " +
                    "The test starts 5 seconds after the button is pressed and lasts for 30 seconds. " +
                    "The start and end of the test are announced with a beep";
            cognitiveInstructions = MessageFormat.format("In your head, count numbers starting from {0}, " +
                    "going down by {1}", startingNum, countdownNum);
        }

        else if (testType == R.string.static_test_tandem_dual_task)
        {
            activityInstructions = "Place the phone in the waistband of your pants. " +
                    "Try to remain as still as possible with one toe touching the heel of the opposite foot. " +
                    "The test starts 5 seconds after the button is pressed and lasts for 30 seconds. " +
                    "The start and end of the test are announced with a beep.";
            cognitiveInstructions = MessageFormat.format("In your head, count numbers starting from {0}, " +
                    "going down by {1}", startingNum, countdownNum);
        }

        else if (testType == R.string.dynamic_test_regular)
        {
            activityInstructions = "Place the phone in the pocket or waistband of your pants. " +
                    "Walk as you normally would for the entire duration of the test. " +
                    "The test starts 5 seconds after the button is pressed and lasts for 30 seconds. " +
                    "The start and end of the test are announced with a beep";
            isDynamicTest = true;
        }

        else if (testType == R.string.dynamic_test_tandem)
        {
            activityInstructions = "Place the phone in the pocket or waistband of your pants. " +
                    "Place the heel of your front foot directly in front of the toe of your back foot for the entire duration of the test. " +
                    "The test starts 5 seconds after the button is pressed and lasts for 30 seconds. " +
                    "The start and end of the test are announced with a beep";
            isDynamicTest = true;
        }

        else if (testType == R.string.dynamic_test_regular_dual_task)
        {
            activityInstructions = "Place the phone in the pocket or waistband of your pants. " +
                    "Walk as you normally would for the entire duration of the test. " +
                    "The test starts 5 seconds after the button is pressed and lasts for 30 seconds. " +
                    "The start and end of the test are announced with a beep";

            cognitiveInstructions = MessageFormat.format("In your head, count numbers starting from {0}, " +
                    "going down by {1}", startingNum, countdownNum);
            isDynamicTest = true;
        }

        else if (testType == R.string.dynamic_test_tandem_dual_task)
        {
            activityInstructions = "Place the phone in the pocket or waistband of your pants. " +
                    "Walk as you normally would for the entire duration of the test. " +
                    "The test starts 5 seconds after the button is pressed and lasts for 30 seconds. " +
                    "The start and end of the test are announced with a beep";

            cognitiveInstructions = MessageFormat.format("In your head, count numbers starting from {0}, " +
                    "going down by {1}", startingNum, countdownNum);
            isDynamicTest = true;
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
        txtCognitiveInstructions.setText(cognitiveInstructions);
    }

    public void btnBaselineTest_onClick(View view) {
        Intent intent;
        if (isDynamicTest) {
            intent = new Intent(this, CameraActivity.class);
        }
        else {
            intent = new Intent(this, TestActivity.class);
        }
        intent.putExtra("test_type", testType);
        intent.putExtra("is_baseline", true);
        startActivityForResult(intent, RUN_STATIC_TEST);
    }

    public void btnPostInjuryTest_onClick(View view) {
        Intent intent;
        if (isDynamicTest) {
            intent = new Intent(this, CameraActivity.class);
        }
        else {
            intent = new Intent(this, TestActivity.class);
        }
        intent.putExtra("test_type", testType);
        intent.putExtra("is_baseline", false);
        startActivityForResult(intent, RUN_DYNAMIC_TEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            finish();
        }
    }
}