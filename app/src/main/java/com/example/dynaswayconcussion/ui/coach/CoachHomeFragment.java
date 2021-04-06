package com.example.dynaswayconcussion.ui.coach;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dynaswayconcussion.R;
import com.example.dynaswayconcussion.ui.CircularImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CoachHomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    TextView hello_textview;
    CircularImageView profile_imageview;

    Button profileCodeButton;
    Button scanCodeButton;

    LinearLayout connectionsLayout;
    List<String> athleteUidList = new ArrayList<>();

    public CoachHomeFragment() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_coach_home, container, false);

        hello_textview = (TextView) view.findViewById(R.id.home_hello_textview);
        profile_imageview = (CircularImageView) view.findViewById(R.id.home_profile_imageview);
        connectionsLayout = (LinearLayout) view.findViewById(R.id.linearLayoutAthleteList);

        scanCodeButton = view.findViewById(R.id.coach_connect_button);
        scanCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        profileCodeButton = view.findViewById(R.id.coach_profile_code_button);
        profileCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        athleteUidList.add("GT8ODAet7scHJpt9yNCvetnuqTr1");
        athleteUidList.add("CDevveNn3FMpNcTaRivb2J4AWa62");
        athleteUidList.add("6uQDOxsPc4Yb1G5LKLI2EoTZ8Lo1");


        loadProfileInfo();
        addButtons();

        return view;
    }

    private void addButtons()
    {
        for (int i=0; i < 3; i++)
        {
            // int btnStyle = R.style.athlete_button;
            // Button btn = new Button(new ContextThemeWrapper(this, btnStyle), null, btnStyle);
            // btn.setBackgroundColor(getColor(R.color.main_green));

            Button btn = new Button(getContext());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 8, 4, 8);
            btn.setLayoutParams(params);

            btn.setBackgroundTintList(this.getResources().getColorStateList(R.color.main_green));
            btn.setOnClickListener(btnAthleteClick);
            btn.setId(i);

            btn.setText("athlete " + i);
            connectionsLayout.addView(btn);
        }
    }

    View.OnClickListener btnAthleteClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getContext(),
                    String.valueOf(v.getId()),
                    Toast.LENGTH_SHORT).show();
        }
    };

    private void loadProfileInfo () {
        db.collection("users").document(mAuth.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String name = (String)document.get("first_name");
                    String image_uri = (String)document.get("profile_image_ref");
                    hello_textview.setText("Welcome back, " + name);
                    if (!image_uri.equals("")) {
                        Picasso.get()
                                .load(image_uri)
                                .resize(250, 250)
                                .centerCrop()
                                .into(profile_imageview);
                    }
                }
            }
        });
    }
}