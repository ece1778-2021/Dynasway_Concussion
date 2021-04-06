package com.example.dynaswayconcussion.ui.coach;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

public class CoachActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    TextView hello_textview;
    CircularImageView profile_imageview;

    Button profileCodeButton;
    Button scanCodeButton;

    LinearLayout connectionsLayout;
    List<String> athleteUidList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        hello_textview = (TextView) findViewById(R.id.home_hello_textview);
        profile_imageview = (CircularImageView) findViewById(R.id.home_profile_imageview);
        connectionsLayout = (LinearLayout) findViewById(R.id.linearLayoutAthleteList);

        scanCodeButton = findViewById(R.id.coach_connect_button);
        scanCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        profileCodeButton = findViewById(R.id.coach_profile_code_button);
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
    }

    private void addButtons()
    {
        for (int i=0; i < 3; i++)
        {
            // int btnStyle = R.style.athlete_button;
            // Button btn = new Button(new ContextThemeWrapper(this, btnStyle), null, btnStyle);
            // btn.setBackgroundColor(getColor(R.color.main_green));

            Button btn = new Button(this);

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
            Toast.makeText(getApplicationContext(),
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