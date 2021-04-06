package com.example.dynaswayconcussion.ui.coach;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;

import android.app.FragmentTransaction;
import android.os.Bundle;

import com.example.dynaswayconcussion.R;
import com.example.dynaswayconcussion.ui.tests.StaticTestSelectionFragment;

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
}