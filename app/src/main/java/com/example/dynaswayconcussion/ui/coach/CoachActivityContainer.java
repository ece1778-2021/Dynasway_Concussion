package com.example.dynaswayconcussion.ui.coach;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.dynaswayconcussion.LoginActivity;
import com.example.dynaswayconcussion.R;
import com.example.dynaswayconcussion.ui.tests.StaticTestSelectionFragment;
import com.google.firebase.auth.FirebaseAuth;

public class CoachActivityContainer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_container);

        Fragment fragment = new CoachHomeFragment();
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content,fragment, "tag")
                .addToBackStack("backstack")
                .commit();
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
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}