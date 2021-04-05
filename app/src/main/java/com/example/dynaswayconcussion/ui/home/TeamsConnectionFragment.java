package com.example.dynaswayconcussion.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dynaswayconcussion.R;
public class TeamsConnectionFragment extends Fragment {

    String uid;
    TextView txtProfileUid;

    public TeamsConnectionFragment() {

    }

    public static TeamsConnectionFragment newInstance(String uid)
    {
        TeamsConnectionFragment fragment = new TeamsConnectionFragment();
        Bundle args = new Bundle();
        args.putString("uid", uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString("uid");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teams_connection, container, false);

        txtProfileUid = view.findViewById(R.id.teams_profile_id);
        txtProfileUid.setText(uid);

        return view;
    }
}