package com.example.dynaswayconcussion.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.dynaswayconcussion.R;
import com.example.dynaswayconcussion.ui.CircularImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

    //Firebase variables
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    //Component related variables
    Button about_concussions;
    Button treatment_options;
    Button get_involved;
    Button concussion_stats;

    TextView hello_textview;

    CircularImageView profile_imageview;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        about_concussions = (Button)view.findViewById(R.id.home_about_concussions_button);
        treatment_options = (Button)view.findViewById(R.id.home_treatment_options_button);
        get_involved = (Button)view.findViewById(R.id.home_get_involved_button);
        concussion_stats = (Button)view.findViewById(R.id.home_concussion_stats_button);
        hello_textview = (TextView)view.findViewById(R.id.home_hello_textview);
        profile_imageview = (CircularImageView)view.findViewById(R.id.home_profile_imageview);

        about_concussions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://concussionsontario.org/patienteducation/aboutconcussions-2/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        treatment_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://concussionfoundation.org/concussion-resources/treatments");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        get_involved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.concussionfoundation.ca/programs-projects/programs");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        concussion_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://concussionsontario.org/access-to-care/concussion-data/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        loadProfileInfo();
        return view;
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