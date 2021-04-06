package com.example.dynaswayconcussion;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dynaswayconcussion.ui.CoachActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final int STEP_COUNTER_PERMISSION_CODE = 0;
    private final int CAMERA_PERMISSION_CODE = 1;
    private final int EXTERNAL_STORAGE_PERMISSION_CODE = 2;
    private final int AUDIO_PERMISSION_CODE = 3;
    private final int DEFAULT_PERMISSION_CODE = 4;

    //Firebase variables
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkIfSensorsPermissionsEnabled();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        DocumentReference ref = db.collection("users").document(mAuth.getCurrentUser().getUid());
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {

                        boolean is_coach = (Boolean)document.getData().get("is_coach");
                        if (is_coach)
                        {
                            Intent intent = new Intent(getApplicationContext(), CoachActivity.class);
                            // clear the stack so the coach can't go back to athlete view
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            getApplicationContext().startActivity(intent);
                        }
                    }
                }
            }
        });

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_test, R.id.navigation_calendar)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    private void checkIfSensorsPermissionsEnabled() {
        ArrayList<String> requestedPermissions = new ArrayList<String>();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            //requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, STEP_COUNTER_PERMISSION_CODE);
            requestedPermissions.add(Manifest.permission.ACTIVITY_RECOGNITION);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            //requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            requestedPermissions.add(Manifest.permission.CAMERA);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            //requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CODE);
            requestedPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            //requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CODE);
            requestedPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            //requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_PERMISSION_CODE);
            requestedPermissions.add(Manifest.permission.RECORD_AUDIO);
        }
        if (requestedPermissions.size() > 0) {
            requestPermissions(requestedPermissions.toArray(new String[0]), AUDIO_PERMISSION_CODE);
        }
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
        boolean isCameraPermissionGranted = false;
        boolean isExternalStoragePermissionGranted = false;
        boolean isAudioPermissionGranted = false;
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
                    if (!isStepCountingGranted) {
                        checkIfSensorsPermissionsEnabled();
                    }
                } else {
                    Toast.makeText(this, "If the permissions aren't enabled, the test can't be done", Toast.LENGTH_SHORT).show();
                }
                return;
            case CAMERA_PERMISSION_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            isCameraPermissionGranted = true;
                        }
                    }
                    // Permission is granted. Continue the action or workflow
                    if (!isCameraPermissionGranted) {
                        checkIfSensorsPermissionsEnabled();
                    }
                } else {
                    Toast.makeText(this, "If the permissions aren't enabled, the test can't be done", Toast.LENGTH_SHORT).show();
                }
                return;
            case EXTERNAL_STORAGE_PERMISSION_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            isExternalStoragePermissionGranted = true;
                        }
                    }
                    // Permission is granted. Continue the action or workflow
                    if (!isExternalStoragePermissionGranted) {
                        checkIfSensorsPermissionsEnabled();
                    }
                } else {
                    Toast.makeText(this, "If the permissions aren't enabled, the test can't be done", Toast.LENGTH_SHORT).show();
                }
                return;
            case AUDIO_PERMISSION_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            isAudioPermissionGranted = true;
                        }
                    }
                    // Permission is granted. Continue the action or workflow
                    if (!isAudioPermissionGranted) {
                        checkIfSensorsPermissionsEnabled();
                    }
                } else {
                    Toast.makeText(this, "If the permissions aren't enabled, the test can't be done", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
}