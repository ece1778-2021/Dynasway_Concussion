package com.example.dynaswayconcussion.ui.tests;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.dynaswayconcussion.R;

public class TestsSelectionFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tests_selection, container, false);

        view.findViewById(R.id.cardViewStaticTestRegular).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.nav_host_fragment, new StaticTestSelectionFragment()).addToBackStack( "backstack" );
                fr.commit();

            }
        });

        view.findViewById(R.id.cardViewDynamicTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.nav_host_fragment, new DynamicTestSelectionFragment()).addToBackStack( "backstack" );
                fr.commit();

            }
        });

        return view;
    }
}