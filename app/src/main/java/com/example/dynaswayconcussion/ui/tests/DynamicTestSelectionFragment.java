package com.example.dynaswayconcussion.ui.tests;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dynaswayconcussion.R;


public class DynamicTestSelectionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dynamic_test_selection, container, false);

        view.findViewById(R.id.cardViewDynamicTestRegular).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), TestInstructionsActivity.class);
                intent.putExtra("test_type", R.string.dynamic_test_regular);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.cardViewDynamicTestTandem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), TestInstructionsActivity.class);
                intent.putExtra("test_type", R.string.dynamic_test_tandem);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.cardViewDynamicTestRegularDual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), TestInstructionsActivity.class);
                intent.putExtra("test_type", R.string.dynamic_test_regular_dual_task);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.cardViewDynamicTestTandemDual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), TestInstructionsActivity.class);
                intent.putExtra("test_type", R.string.dynamic_test_tandem_dual_task);
                startActivity(intent);
            }
        });
        return view;
    }
}