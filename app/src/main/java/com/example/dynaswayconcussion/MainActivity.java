package com.example.dynaswayconcussion;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private final int STEP_COUNTER_PERMISSION_CODE = 0;

    //Firebase variables
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mAuth = FirebaseAuth.getInstance();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.profile_activity_menu is a reference to an xml file named profile_activity_menu.xml
        // which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.profile_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle menu button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //If log out button on action bar pressed, log out from Firebase and exit profile activity
        if (id == R.id.logoutButton) {
            mAuth.signOut();
            finish();
        }
        return super.onOptionsItemSelected(item);
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