package com.example.dynaswayconcussion;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private final int STEP_COUNTER_PERMISSION_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_test, R.id.navigation_calendar)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

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
}