package com.example.dynaswayconcussion.ui.tests;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.dynaswayconcussion.R;

public class TestsFragment extends Fragment {

    private TestsViewModel testsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        testsViewModel =
                new ViewModelProvider(this).get(TestsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tests, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        testsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}