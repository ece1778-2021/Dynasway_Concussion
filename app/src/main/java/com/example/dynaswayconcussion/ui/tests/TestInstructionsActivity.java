package com.example.dynaswayconcussion.ui.tests;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
        int testTypeName = getIntent().getIntExtra("test_type_name", -1);
        String title = getString(testTypeName);
        TextView txtTitle = findViewById(R.id.txtTestTitle);
        txtTitle.setText(title);

        String activityInstructions = "";
        String cognitiveInstructions = "None";

        if (testType == R.string.static_test_regular_constant)
        {
            activityInstructions = "Place the phone in the waistband of your pants. " +
                    "Try to remain as still as possible with your feet shoulder width apart. " +
                    "The test starts 5 seconds after the button is pressed and lasts for 30 seconds. " +
                    "The start and end of the test are announced with a beep.";
        }

        else if (testType == R.string.static_test_tandem_constant)
        {
            activityInstructions = "Place the phone in the waistband of your pants. " +
                    "Try to remain as still as possible with one toe touching the heel of the opposite foot. " +
                    "The test starts 5 seconds after the button is pressed and lasts for 30 seconds. " +
                    "The start and end of the test are announced with a beep.";
        }

        else if (testType == R.string.static_test_regular_dual_task_constant)
        {
            activityInstructions = "Place the phone in the waistband of your pants. " +
                    "Try to remain as still as possible with one toe touching the heel of the opposite foot. " +
                    "The test starts 5 seconds after the button is pressed and lasts for 30 seconds. " +
                    "The start and end of the test are announced with a beep";
            cognitiveInstructions = MessageFormat.format("In your head, count numbers starting from {0}, " +
                    "going down by {1}", startingNum, countdownNum);
        }

        else if (testType == R.string.static_test_tandem_dual_task_constant)
        {
            activityInstructions = "Place the phone in the waistband of your pants. " +
                    "Try to remain as still as possible with one toe touching the heel of the opposite foot. " +
                    "The test starts 5 seconds after the button is pressed and lasts for 30 seconds. " +
                    "The start and end of the test are announced with a beep.";
            cognitiveInstructions = MessageFormat.format("In your head, count numbers starting from {0}, " +
                    "going down by {1}", startingNum, countdownNum);
        }

        else if (testType == R.string.dynamic_test_regular_constant)
        {
            activityInstructions = "Place the phone in a position where the camera will be able to see " +
                    "a space of about 3 meters, and record the video inside the app or through the phone's camera app and then select it from the gallery. " +
                    "You will need to start walking for three to five steps before entering the camera's view, and continue walking until you're " +
                    "outside of the camera's view. A confirmation will be shown in case the video is incorrect.";
            isDynamicTest = true;
        }

        else if (testType == R.string.dynamic_test_tandem_constant)
        {
            activityInstructions = "Place the phone in a position where the camera will be able to see " +
                    "a space of about 3 meters, and record the video inside the app or through the phone's camera app and then select it from the gallery. " +
                    "You will need to start walking for three to five steps before entering the camera's view, and continue walking until you're " +
                    "outside of the camera's view. A confirmation will be shown in case the video is incorrect.";
            isDynamicTest = true;
        }

        else if (testType == R.string.dynamic_test_regular_dual_task_constant)
        {
            activityInstructions = "Place the phone in a position where the camera will be able to see " +
                    "a space of about 3 meters, and record the video inside the app or through the phone's camera app and then select it from the gallery. " +
                    "You will need to start walking for three to five steps before entering the camera's view, and continue walking until you're " +
                    "outside of the camera's view. A confirmation will be shown in case the video is incorrect.";

            cognitiveInstructions = MessageFormat.format("In your head, count numbers starting from {0}, " +
                    "going down by {1}", startingNum, countdownNum);
            isDynamicTest = true;
        }

        else if (testType == R.string.dynamic_test_tandem_dual_task_constant)
        {
            activityInstructions = "Place the phone in a position where the camera will be able to see " +
                    "a space of about 3 meters, and record the video inside the app or through the phone's camera app and then select it from the gallery. " +
                    "You will need to start walking for three to five steps before entering the camera's view, and continue walking until you're " +
                    "outside of the camera's view. A confirmation will be shown in case the video is incorrect once it has been recorded or selected.";

            cognitiveInstructions = MessageFormat.format("In your head, count numbers starting from {0}, " +
                    "going down by {1}", startingNum, countdownNum);
            isDynamicTest = true;
        }

        else
        {
            try {
                Toast.makeText(TestInstructionsActivity.this, "Issue when loading test.",
                        Toast.LENGTH_SHORT).show();
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