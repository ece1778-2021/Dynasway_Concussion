package com.example.dynaswayconcussion.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dynaswayconcussion.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class CoachActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    TextView hello_textview;
    CircularImageView profile_imageview;

    LinearLayout connectionsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        hello_textview = (TextView) findViewById(R.id.home_hello_textview);
        profile_imageview = (CircularImageView) findViewById(R.id.home_profile_imageview);
        connectionsLayout = (LinearLayout) findViewById(R.id.linearLayoutAthleteList);

        loadProfileInfo();
        addButtons();
    }

    private void addButtons()
    {
        for (int i=0; i < 3; i++)
        {
            // int btnStyle = R.style.athlete_button;
            // Button btn = new Button(new ContextThemeWrapper(this, btnStyle), null, btnStyle);
            Button btn = new Button(this);
            btn.setBackgroundColor(getColor(R.color.main_green));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(36, 36, 36, 36);

            btn.setLayoutParams(params);
            btn.setText("athlete 1");
            connectionsLayout.addView(btn);
        }
    }

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